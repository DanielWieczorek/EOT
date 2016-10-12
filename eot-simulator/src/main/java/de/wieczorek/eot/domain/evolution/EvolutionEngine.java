package de.wieczorek.eot.domain.evolution;

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
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;

public class EvolutionEngine {

    private final IExchange exchange;

    @Inject
    public EvolutionEngine(final IExchange exchange) {
	this.exchange = exchange;
    }

    public List<Trader> getNextPopulation(final int size, final List<Trader> traders) {
	return traders; // TODO
    }

    public List<Trader> getInitialPopulation(final int size) {
	final List<Trader> currentGeneration = new LinkedList<>();

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

	    currentGeneration.add(newTrader);

	}
	return currentGeneration;
    }
}
