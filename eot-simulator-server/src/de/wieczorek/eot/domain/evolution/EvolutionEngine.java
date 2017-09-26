package de.wieczorek.eot.domain.evolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Account;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork.NetworkType;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.comparator.BinaryComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.ComparatorType;
import de.wieczorek.eot.domain.trading.rule.metric.BollingerPercentGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.DiffToMaxGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.MacdGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.StochasticFastGraphMetric;

public class EvolutionEngine {

    private static final Logger logger = Logger.getLogger(EvolutionEngine.class.getName());
    private final IExchange exchange;

    @Inject
    public EvolutionEngine(final IExchange exchange) {
	this.exchange = exchange;
    }

    public final List<IIndividual> getNextPopulation(final int size, final List<IIndividual> traders) {

	logger.severe("input for next Trader generation:");
	for (final IIndividual individual : traders) {
	    logger.severe("" + individual.getName() + ": rating:" + individual.calculateFitness() + " trades:"
		    + individual.getNumberOfTrades() + " profit: " + individual.getNetProfit());
	}

	final List<IIndividual> result = new ArrayList<>();
	result.addAll(traders);
	final Random r = new Random(System.currentTimeMillis());
	for (final IIndividual individual : traders) {
	    if (size > result.size()) {
		IIndividual temp = traders.get(r.nextInt(traders.size()));
		result.addAll(individual.combineWith(temp));
	    }
	}
	// result.addAll(traders);

	for (final IIndividual individual : result) {
	    final Trader t = (Trader) individual;
	    t.getAccount().clear();
	    t.getAccount().deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    t.setPerformance(new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1)));
	    if (r.nextInt(11) == 10 && traders.indexOf(individual) > 5) {
		individual.mutate();
	    }

	}

	return result.stream().filter(distinctByKey(s -> ((Trader) s).getName())).collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
	final Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public List<IIndividual> getInitialPopulation(final int size) {
	final List<IIndividual> currentGeneration = new LinkedList<>();

	final int sizePerAlgorithm = size;

	NetworkType[] types = NetworkType.values();
	for (int observedMinutes = 11; observedMinutes < 48 * 60; observedMinutes += 8 * 60) {
	    for (int i = 0; i < types.length; i++) {
		for (int chunkSize = 1; chunkSize <= 10; chunkSize += 2) {
		    buildRsiTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.LESS,
			    ComparatorType.GREATER, types[i], chunkSize);
		    buildRsiTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.GREATER,
			    ComparatorType.LESS, types[i], chunkSize);

		    buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.LESS,
			    ComparatorType.GREATER, types[i], chunkSize);
		    buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
			    ComparatorType.GREATER, ComparatorType.LESS, types[i], chunkSize);

		    buildCoppocTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.LESS,
			    ComparatorType.GREATER, types[i], chunkSize);
		    buildCoppocTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.GREATER,
			    ComparatorType.LESS, types[i], chunkSize);

		    buildMacdTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.GREATER,
			    ComparatorType.LESS, types[i], chunkSize);
		    buildMacdTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.LESS,
			    ComparatorType.GREATER, types[i], chunkSize);

		    buildBollingerPercentTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
			    ComparatorType.LESS, ComparatorType.GREATER, types[i], chunkSize);
		    buildBollingerPercentTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
			    ComparatorType.GREATER, ComparatorType.LESS, types[i], chunkSize);

		    diffToMaxTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.LESS,
			    ComparatorType.GREATER, types[i], chunkSize);
		    diffToMaxTrader(currentGeneration, sizePerAlgorithm, observedMinutes, ComparatorType.GREATER,
			    ComparatorType.LESS, types[i], chunkSize);
		}
	    }
	}
	return currentGeneration;
    }

    private void buildMacdTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type, int chunkSize) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new BinaryComparator(-10.0 + (i / 10.0), buyComparator));
	    rule1.setMetric(new MacdGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new BinaryComparator(10.0 - i / 10.0, sellComparator));
	    rule21.setMetric(new MacdGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2, n);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }

	    final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);

	    currentGeneration.add(newTrader);
	    newTrader.setNumberOfChunks(chunkSize);

	}
    }

    private void buildCoppocTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type, int chunkSize) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new BinaryComparator(-10.0 + (i / 10.0), buyComparator));
	    rule1.setMetric(new CoppocGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new BinaryComparator(10.0 - (i / 10.0), sellComparator));
	    rule21.setMetric(new CoppocGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2, n);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }

	    final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);

	    currentGeneration.add(newTrader);
	    newTrader.setNumberOfChunks(chunkSize);
	}
    }

    private void buildStochasticFastTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type, int chunkSize) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new BinaryComparator(0.0 + (i / (Math.max(1, 100 / sizePerAlgorithm))), buyComparator));
	    rule1.setMetric(new StochasticFastGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(
		    new BinaryComparator(100.0 - i / (Math.max(1, 100 / sizePerAlgorithm)), sellComparator));
	    rule21.setMetric(new StochasticFastGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2, n);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }

	    final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);

	    currentGeneration.add(newTrader);
	    newTrader.setNumberOfChunks(chunkSize);

	}
    }

    private void buildRsiTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type, int chunkSize) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new BinaryComparator(i, buyComparator));
	    rule1.setMetric(new RsiGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new BinaryComparator(100 - i, sellComparator));
	    rule21.setMetric(new RsiGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2, n);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }
	    final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);

	    newTrader.setNumberOfChunks(chunkSize);
	    currentGeneration.add(newTrader);

	}
    }

    private void buildBollingerPercentTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm,
	    int n, ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type, int chunkSize) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new BinaryComparator(i, buyComparator));
	    rule1.setMetric(new BollingerPercentGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new BinaryComparator(100 - i, sellComparator));
	    rule21.setMetric(new BollingerPercentGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2, n);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }
	    final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);

	    newTrader.setNumberOfChunks(chunkSize);
	    currentGeneration.add(newTrader);

	}
    }

    private void diffToMaxTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type, int chunkSize) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new BinaryComparator(i - 50, buyComparator));
	    rule1.setMetric(new DiffToMaxGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new BinaryComparator(50 - i, sellComparator));
	    rule21.setMetric(new DiffToMaxGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2, n);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }
	    final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);

	    newTrader.setNumberOfChunks(chunkSize);
	    currentGeneration.add(newTrader);

	}
    }

}
