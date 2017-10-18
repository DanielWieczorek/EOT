package de.wieczorek.eot.ui.trader;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.IAccount;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.comparator.BinaryComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.ChangeComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.ITradingRuleComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.RangeComparator;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.BollingerPercentGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.DiffToMaxGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.GraphMetricType;
import de.wieczorek.eot.domain.trading.rule.metric.MacdGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.StochasticFastGraphMetric;

public class TraderFactory {

    public static Trader createTrader(TraderConfiguration config, IAccount wallet, IExchange exchange) {
	return buildTrader(config, wallet, exchange);
    }

    private static Trader buildTrader(TraderConfiguration config, IAccount wallet, IExchange exchange) {

	wallet.deposit(new ExchangableSet(config.getExchangablesToTrade().getTo(), 1.0));
	final TradingPerformance performance = new TradingPerformance(
		new ExchangableSet(config.getExchangablesToTrade().getTo(), 1));

	TraderNeuralNetwork buyNetwork = buildNeuralNetwork(config.getBuyNetwork());
	TraderNeuralNetwork sellNetwork = buildNeuralNetwork(config.getSellNetwork());

	Trader newTrader = new Trader(wallet, exchange, buyNetwork, sellNetwork, config.getExchangablesToTrade(),
		performance, config.isStopLossActivated());
	newTrader.setExchange(exchange);
	newTrader.setNumberOfChunks(config.getNumberOfChunks());
	return newTrader;
    }

    private static TraderNeuralNetwork buildNeuralNetwork(NeuralNetworkConfiguration config) {
	TradingRulePerceptron perceptron1 = buildPerceptron(config.getPerceptron1());
	TradingRulePerceptron perceptron2 = buildPerceptron(config.getPerceptron2());

	return new TraderNeuralNetwork(perceptron1, perceptron2, config.getType());
    }

    private static TradingRulePerceptron buildPerceptron(TradingRulePerceptronConfiguration perceptron) {
	final TradingRulePerceptron result = new TradingRulePerceptron(perceptron.getThreshold());
	result.setObservationTime(perceptron.getObservationTime());

	for (InputConfiguration input : perceptron.getInputs()) {
	    ITradingRuleComparator comparator = buildComparator(input.getComparator());
	    AbstractGraphMetric metric = buildMetric(input.getType());
	    TradingRule rule = new TradingRule();
	    rule.setComparator(comparator);
	    rule.setMetric(metric);

	    result.add(rule, input.getWeight());
	}
	return result;
    }

    private static AbstractGraphMetric buildMetric(GraphMetricType type) {
	switch (type) {
	case BollingerPercent:
	    return new BollingerPercentGraphMetric();
	case RSI:
	    return new RsiGraphMetric();
	case Coppoch:
	    return new CoppocGraphMetric();
	case StochasticFast:
	    return new StochasticFastGraphMetric();
	case MACD:
	    return new MacdGraphMetric();
	case DiffToMax:
	    return new DiffToMaxGraphMetric();
	}
	return null;
    }

    private static ITradingRuleComparator buildComparator(ComparatorConfiguration comparator) {
	if (comparator.getComparator().equals(ComparatorConfigurationType.Binary)) {
	    return new BinaryComparator(comparator.getThreshold1(), comparator.getBinaryType());
	} else if (comparator.getComparator().equals(ComparatorConfigurationType.Range)) {
	    return new RangeComparator(comparator.getThreshold1(), comparator.getThreshold2());

	} else {
	    return new ChangeComparator(comparator.getThreshold1(), comparator.getThreshold2(),
		    comparator.getChangeType());
	}
    }
}
