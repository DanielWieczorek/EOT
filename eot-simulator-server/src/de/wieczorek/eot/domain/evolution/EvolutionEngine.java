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
import de.wieczorek.eot.domain.trading.rule.ComparatorType;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork.NetworkType;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
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
		logger.severe("combining " + individual.getName() + " and " + temp.getName());
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
	for (int n = 11; n < 25 * 60 * 7; n += 8 * 60 * 14) {
	    for (int i = 0; i < types.length; i++) {
		buildRsiTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.LESS, ComparatorType.GREATER,
			types[i]);
		buildRsiTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.GREATER, ComparatorType.LESS,
			types[i]);

		buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.LESS,
			ComparatorType.GREATER, types[i]);
		buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.GREATER,
			ComparatorType.LESS, types[i]);

		buildCoppocTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.LESS, ComparatorType.GREATER,
			types[i]);
		buildCoppocTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.GREATER, ComparatorType.LESS,
			types[i]);

		buildMacdTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.GREATER, ComparatorType.LESS,
			types[i]);
		buildMacdTrader(currentGeneration, sizePerAlgorithm, n, ComparatorType.LESS, ComparatorType.GREATER,
			types[i]);
	    }

	}

	return currentGeneration;
    }

    private void buildMacdTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(-10.0 + (i / 10.0));
	    rule1.setComparator(buyComparator);
	    rule1.setMetric(new MacdGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(10.0 - i / 10.0);
	    rule21.setComparator(sellComparator);
	    rule21.setMetric(new MacdGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }

	    final Trader newTrader = new Trader("MACD_" + i + "_" + (100 - i), wallet, exchange, buyNetwork,
		    sellNetwork, new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setNumberOfObservedMinutes(n);
	    newTrader.setName(newTrader.generateDescriptiveName());
	    currentGeneration.add(newTrader);

	}
    }

    private void buildCoppocTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(-10.0 + (i / 10.0));
	    rule1.setComparator(buyComparator);
	    rule1.setMetric(new CoppocGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(10.0 - i / 10.0);
	    rule21.setComparator(sellComparator);
	    rule21.setMetric(new CoppocGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }

	    final Trader newTrader = new Trader("coppoch_" + i + "_" + (100 - i), wallet, exchange, buyNetwork,
		    sellNetwork, new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setNumberOfObservedMinutes(n);
	    newTrader.setName(newTrader.generateDescriptiveName());
	    currentGeneration.add(newTrader);

	}
    }

    private void buildStochasticFastTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(0.0 + (i / (Math.max(1, 100 / sizePerAlgorithm))));
	    rule1.setComparator(buyComparator);
	    rule1.setMetric(new StochasticFastGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(100.0 - i / (Math.max(1, 100 / sizePerAlgorithm)));
	    rule21.setComparator(sellComparator);
	    rule21.setMetric(new StochasticFastGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }

	    final Trader newTrader = new Trader("FAST_" + i + "_" + (100 - i), wallet, exchange, buyNetwork,
		    sellNetwork, new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setNumberOfObservedMinutes(n);
	    newTrader.setName(newTrader.generateDescriptiveName());
	    currentGeneration.add(newTrader);

	}
    }

    private void buildRsiTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ComparatorType buyComparator, ComparatorType sellComparator, NetworkType type) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(i);
	    rule1.setComparator(buyComparator);
	    rule1.setMetric(new RsiGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(100 - i);
	    rule21.setComparator(sellComparator);
	    rule21.setMetric(new RsiGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);

	    final TradingRulePerceptron neverFiring = new TradingRulePerceptron(rule21, 1, 2);
	    TraderNeuralNetwork buyNetwork = null;
	    TraderNeuralNetwork sellNetwork = null;
	    if (type.equals(NetworkType.AND) || type.equals(NetworkType.OR)) {
		buyNetwork = new TraderNeuralNetwork(buyRule, buyRule, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, sellRule, type);
	    } else {
		buyNetwork = new TraderNeuralNetwork(buyRule, neverFiring, type);
		sellNetwork = new TraderNeuralNetwork(sellRule, neverFiring, type);
	    }
	    final Trader newTrader = new Trader("rsi_" + i + "_" + (100 - i), wallet, exchange, buyNetwork, sellNetwork,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setNumberOfObservedMinutes(n);
	    newTrader.setName(newTrader.generateDescriptiveName());
	    currentGeneration.add(newTrader);

	}
    }
}
