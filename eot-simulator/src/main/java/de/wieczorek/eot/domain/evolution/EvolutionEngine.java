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

import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Account;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trading.rule.ComparatorType;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
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
	    logger.severe("" + individual.getName() + ": " + individual.calculateFitness());
	}

	final List<IIndividual> result = new ArrayList<>();
	traders.addAll(traders);
	final Random r = new Random(System.currentTimeMillis());
	for (final IIndividual individual : traders) {
	    if (size < traders.size() + traders.size()) {
		result.addAll(individual.combineWith(traders.get(r.nextInt(traders.size()))));
	    }
	}
	result.addAll(traders);

	for (final IIndividual individual : result) {
	    final Trader t = (Trader) individual;
	    t.getAccount().clear();
	    t.getAccount().deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
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

	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(i);
	    rule1.setComparator(ComparatorType.LESS);
	    rule1.setMetric(new RsiGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(100 - i);
	    rule21.setComparator(ComparatorType.GREATER);
	    rule21.setMetric(new RsiGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);
	    final Trader newTrader = new Trader("rsi_" + i + "_" + (100 - i), wallet, exchange, buyRule, sellRule,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setName(newTrader.generateDescriptiveName());

	    currentGeneration.add(newTrader);

	}

	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(0.0 + (i / (Math.max(1, 100 / sizePerAlgorithm))));
	    rule1.setComparator(ComparatorType.LESS);
	    rule1.setMetric(new StochasticFastGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(100.0 - i / (Math.max(1, 100 / sizePerAlgorithm)));
	    rule21.setComparator(ComparatorType.GREATER);
	    rule21.setMetric(new StochasticFastGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);
	    final Trader newTrader = new Trader("FAST_" + i + "_" + (100 - i), wallet, exchange, buyRule, sellRule,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setName(newTrader.generateDescriptiveName());
	    currentGeneration.add(newTrader);

	}

	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Account wallet = new Account();
	    wallet.deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
	    final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	    final TradingRule rule1 = new TradingRule();
	    rule1.setThreshold(-10.0 + (i / 10.0));
	    rule1.setComparator(ComparatorType.LESS);
	    rule1.setMetric(new CoppocGraphMetric());

	    final TradingRule rule21 = new TradingRule();
	    rule21.setThreshold(10.0 - i / 10.0);
	    rule21.setComparator(ComparatorType.GREATER);
	    rule21.setMetric(new CoppocGraphMetric());

	    final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	    final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);
	    final Trader newTrader = new Trader("coppoch_" + i + "_" + (100 - i), wallet, exchange, buyRule, sellRule,
		    new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	    newTrader.setExchange(exchange);
	    newTrader.setName(newTrader.generateDescriptiveName());
	    currentGeneration.add(newTrader);

	}

	return currentGeneration;
    }
}
