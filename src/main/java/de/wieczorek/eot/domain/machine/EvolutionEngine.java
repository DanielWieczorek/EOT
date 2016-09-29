package de.wieczorek.eot.domain.machine;

import java.util.LinkedList;
import java.util.List;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.ExchangablePair;
import de.wieczorek.eot.domain.trader.ComparatorType;
import de.wieczorek.eot.domain.trader.ExchangableAmount;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.RsiGraphMetric;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trader.TradingRule;
import de.wieczorek.eot.domain.trader.TradingRulePerceptron;
import de.wieczorek.eot.domain.trader.Wallet;

public class EvolutionEngine {

	private IExchange exchange;

	public EvolutionEngine(IExchange exchange) {
		this.exchange = exchange;
	}

	public List<Trader> getNextPopulation(int size, List<Trader> traders) {
		return traders; // TODO
	}

	public List<Trader> getInitialPopulation(int size) {
		List<Trader> currentGeneration = new LinkedList<>();

		int sizePerAlgorithm = size;

		for (int i = 0; i < sizePerAlgorithm; i++) {
			Wallet wallet = new Wallet();
			wallet.deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
			TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

			TradingRule rule1 = new TradingRule();
			rule1.setThreshold(i);
			rule1.setComparator(ComparatorType.LESS);
			rule1.setMetric(new RsiGraphMetric());

			TradingRule rule21 = new TradingRule();
			rule21.setThreshold(100 - i);
			rule21.setComparator(ComparatorType.GREATER);
			rule21.setMetric(new RsiGraphMetric());

			TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
			TradingRulePerceptron sellRule = new TradingRulePerceptron(rule21, 1, 1);
			Trader newTrader = new Trader("rsi_" + i + "_" + (100 - i), wallet, exchange, buyRule, sellRule,
					new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
			newTrader.setExchange(exchange);

			currentGeneration.add(newTrader);

		}
		return currentGeneration;
	}
}
