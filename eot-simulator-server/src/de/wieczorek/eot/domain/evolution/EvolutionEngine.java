package de.wieczorek.eot.domain.evolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.wieczorek.eot.business.IBusinessLayerFacade;
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
import de.wieczorek.eot.domain.trading.rule.comparator.BinaryComparatorType;
import de.wieczorek.eot.domain.trading.rule.comparator.ChangeComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.ChangeComparatorType;
import de.wieczorek.eot.domain.trading.rule.metric.BollingerPercentGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.DiffToMaxGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.MacdGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.StochasticFastGraphMetric;

public class EvolutionEngine {

    private static final Logger logger = Logger.getLogger(EvolutionEngine.class.getName());
    private final IExchange exchange;
    private IBusinessLayerFacade facade;

    @Inject
    public EvolutionEngine(final IExchange exchange, IBusinessLayerFacade facade) {
	this.exchange = exchange;
	this.facade = facade;
    }

    public final List<IIndividual> getNextPopulation(final int size, final List<IIndividual> traders) {

	logger.fatal("input for next Trader generation:");
	for (final IIndividual individual : traders) {
	    logger.fatal("" + individual.getName() + ": rating:" + individual.calculateFitness() + " trades:"
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

	List<ExchangableSet> amounts = facade.getAccountBalance();
	for (final IIndividual individual : result) {
	    final Trader t = (Trader) individual;
	    t.getAccount().clear();
	    amounts.forEach(amount -> t.getAccount().deposit(amount));
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
	List<ExchangableSet> amounts = facade.getAccountBalance();
	NetworkType[] types = NetworkType.values();
	for (int observedMinutes = 11; observedMinutes < 48 * 60; observedMinutes += observedMinutes * 2) {
	    for (int stopLoss = 0; stopLoss < 2; stopLoss++) {
		for (int sellRuleNull = 0; sellRuleNull < 2; sellRuleNull++) {
		    for (int i = 0; i < types.length; i++) {
			for (int chunkSize = 1; chunkSize <= 10; chunkSize += 2) {
			    buildRsiTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.LESS, BinaryComparatorType.GREATER, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);
			    buildRsiTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.GREATER, BinaryComparatorType.LESS, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);

			    buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.LESS, BinaryComparatorType.GREATER, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);
			    buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.GREATER, BinaryComparatorType.LESS, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);

			    buildCoppocTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.LESS, BinaryComparatorType.GREATER, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);
			    buildCoppocTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.GREATER, BinaryComparatorType.LESS, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);

			    buildMacdTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.GREATER, BinaryComparatorType.LESS, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);
			    buildMacdTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.LESS, BinaryComparatorType.GREATER, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);

			    buildBollingerPercentTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.LESS, BinaryComparatorType.GREATER, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);
			    buildBollingerPercentTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.GREATER, BinaryComparatorType.LESS, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);

			    diffToMaxTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.LESS, BinaryComparatorType.GREATER, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);
			    diffToMaxTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    BinaryComparatorType.GREATER, BinaryComparatorType.LESS, types[i], chunkSize,
				    stopLoss == 0, sellRuleNull == 0, amounts);

			    buildRsiTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.FallBelowThreshold, ChangeComparatorType.RiseAboveThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    buildRsiTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.RiseAboveThreshold, ChangeComparatorType.FallBelowThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);

			    buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.FallBelowThreshold, ChangeComparatorType.RiseAboveThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    buildStochasticFastTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.RiseAboveThreshold, ChangeComparatorType.FallBelowThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);

			    buildCoppocTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.FallBelowThreshold, ChangeComparatorType.RiseAboveThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    buildCoppocTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.RiseAboveThreshold, ChangeComparatorType.FallBelowThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    //
			    buildMacdTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.RiseAboveThreshold, ChangeComparatorType.FallBelowThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    buildMacdTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.FallBelowThreshold, ChangeComparatorType.RiseAboveThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);

			    buildBollingerPercentTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.FallBelowThreshold, ChangeComparatorType.RiseAboveThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    buildBollingerPercentTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.RiseAboveThreshold, ChangeComparatorType.FallBelowThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);

			    diffToMaxTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.FallBelowThreshold, ChangeComparatorType.RiseAboveThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);
			    diffToMaxTrader(currentGeneration, sizePerAlgorithm, observedMinutes,
				    ChangeComparatorType.RiseAboveThreshold, ChangeComparatorType.FallBelowThreshold,
				    types[i], chunkSize, stopLoss == 0, sellRuleNull == 0, amounts);

			}
		    }
		}
	    }
	}
	return currentGeneration;
    }

    private void buildMacdTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    BinaryComparatorType buyComparator, BinaryComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
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

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildMacdTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ChangeComparatorType buyComparator, ChangeComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new ChangeComparator(-10.0 + (i / 10.0), 0.05, buyComparator));
	    rule1.setMetric(new MacdGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new ChangeComparator(10.0 - i / 10.0, 0.05, sellComparator));
	    rule21.setMetric(new MacdGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildCoppocTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    BinaryComparatorType buyComparator, BinaryComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
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

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);
	}
    }

    private void buildCoppocTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ChangeComparatorType buyComparator, ChangeComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new ChangeComparator(-10.0 + (i / 10.0), 0.05, buyComparator));
	    rule1.setMetric(new CoppocGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new ChangeComparator(10.0 - (i / 10.0), 0.05, sellComparator));
	    rule21.setMetric(new CoppocGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);
	}
    }

    private void buildStochasticFastTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    BinaryComparatorType buyComparator, BinaryComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
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

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildStochasticFastTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ChangeComparatorType buyComparator, ChangeComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(
		    new ChangeComparator(0.0 + (i / (Math.max(1, 100 / sizePerAlgorithm))), 0.5, buyComparator));
	    rule1.setMetric(new StochasticFastGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(
		    new ChangeComparator(100.0 - i / (Math.max(1, 100 / sizePerAlgorithm)), 0.5, sellComparator));
	    rule21.setMetric(new StochasticFastGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildRsiTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    BinaryComparatorType buyComparator, BinaryComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
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

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildRsiTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ChangeComparatorType buyComparator, ChangeComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new ChangeComparator(i, 0.5, buyComparator));
	    rule1.setMetric(new RsiGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new ChangeComparator(100 - i, 0.5, sellComparator));
	    rule21.setMetric(new RsiGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildBollingerPercentTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm,
	    int n, BinaryComparatorType buyComparator, BinaryComparatorType sellComparator, NetworkType type,
	    int chunkSize, boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
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

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void buildBollingerPercentTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm,
	    int n, ChangeComparatorType buyComparator, ChangeComparatorType sellComparator, NetworkType type,
	    int chunkSize, boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
	for (int i = 0; i < sizePerAlgorithm; i++) {

	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new ChangeComparator(i, 0.5, buyComparator));
	    rule1.setMetric(new BollingerPercentGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new ChangeComparator(100 - i, 0.5, sellComparator));
	    rule21.setMetric(new BollingerPercentGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);

	}
    }

    private void diffToMaxTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    BinaryComparatorType buyComparator, BinaryComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
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

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);
	}
    }

    private void diffToMaxTrader(final List<IIndividual> currentGeneration, final int sizePerAlgorithm, int n,
	    ChangeComparatorType buyComparator, ChangeComparatorType sellComparator, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, List<ExchangableSet> amounts) {
	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setComparator(new ChangeComparator(i - 50, 0.25, buyComparator));
	    rule1.setMetric(new DiffToMaxGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setComparator(new ChangeComparator(50 - i, 0.25, sellComparator));
	    rule21.setMetric(new DiffToMaxGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1, n);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1, n);
	    buyRule.setObservationTime(n);
	    sellRule.setObservationTime(n);

	    buildAndAddTrader(currentGeneration, n, type, chunkSize, isStopLossActivated, isSellRuleNull, performance,
		    rule21, buyRule, sellRule, amounts);
	}
    }

    private void buildAndAddTrader(final List<IIndividual> currentGeneration, int n, NetworkType type, int chunkSize,
	    boolean isStopLossActivated, boolean isSellRuleNull, final TradingPerformance performance,
	    final TradingRule rule21, final TradingRulePerceptron buyRule, final TradingRulePerceptron sellRule,
	    List<ExchangableSet> amounts) {

	final Account wallet = new Account();
	amounts.forEach(amount -> wallet.deposit(amount));
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
	if (isSellRuleNull) {
	    sellNetwork = null;
	}

	final Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance, isStopLossActivated);
	newTrader.setExchange(exchange);

	currentGeneration.add(newTrader);
	newTrader.setNumberOfChunks(chunkSize);
    }

}
