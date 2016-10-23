package de.wieczorek.eot.domain.evolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trader.Wallet;
import de.wieczorek.eot.domain.trading.rule.ComparatorType;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;

public class EvolutionEngine {

    private final IExchange exchange;

    @Inject
    public EvolutionEngine(final IExchange exchange) {
	this.exchange = exchange;
    }

    public List<IIndividual> getNextPopulation(final int size, final List<IIndividual> traders) {
	final List<IIndividual> result = new ArrayList<>();
	for (final IIndividual individual : traders) {
	    for (int i = 0; i < traders.size(); i++) {
		result.addAll(individual.combineWith(traders.get(i)));
	    }
	}

	for (final IIndividual individual : result) {
	    final Trader t = (Trader) individual;
	    t.getWallet().clear();
	    t.getWallet().deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
	    t.setPerformance(new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1)));
	    individual.mutate();

	}
	return result;
    }

    public List<IIndividual> getInitialPopulation(final int size) {
	final List<IIndividual> currentGeneration = new LinkedList<>();

	final int sizePerAlgorithm = size;

	for (int i = 0; i < sizePerAlgorithm; i++) {
	    final Wallet wallet = new Wallet();
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
	    final Wallet wallet = new Wallet();
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
