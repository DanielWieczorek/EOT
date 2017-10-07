package de.wieczorek.eot.ui.trader;

import java.util.ArrayList;
import java.util.List;

import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron.Input;
import de.wieczorek.eot.domain.trading.rule.comparator.BinaryComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.ITradingRuleComparator;
import de.wieczorek.eot.domain.trading.rule.comparator.RangeComparator;

public class TraderConfigurationFactory {

    public static TraderConfiguration createTraderConfiguration(Trader config) {
	return buildTraderConfiguration(config);
    }

    private static TraderConfiguration buildTraderConfiguration(Trader trader) {
	TraderConfiguration result = new TraderConfiguration();
	result.setExchangablesToTrade(trader.getExchangablesToTrade());
	result.setNumberOfChunks(trader.getNumberOfChunks());
	result.setBuyNetwork(buildNetworkConfiguration(trader.getBuyRule()));
	result.setSellNetwork(buildNetworkConfiguration(trader.getSellRule()));
	result.setStopLossActivated(trader.isStopLossActivated());

	return result;
    }

    private static NeuralNetworkConfiguration buildNetworkConfiguration(TraderNeuralNetwork traderNeuralNetwork) {
	NeuralNetworkConfiguration result = new NeuralNetworkConfiguration();
	result.setPerceptron1(buildPerceptronConfiguration(traderNeuralNetwork.getPerceptron1()));
	result.setPerceptron2(buildPerceptronConfiguration(traderNeuralNetwork.getPerceptron2()));
	result.setType(traderNeuralNetwork.getType());
	return result;
    }

    private static TradingRulePerceptronConfiguration buildPerceptronConfiguration(TradingRulePerceptron perceptron) {
	TradingRulePerceptronConfiguration result = new TradingRulePerceptronConfiguration();
	result.setObservationTime(perceptron.getObservationTime());
	result.setThreshold(perceptron.getThreshold());
	result.setInputs(buildInputConfigurations(perceptron.getInputs()));

	return result;
    }

    private static List<InputConfiguration> buildInputConfigurations(List<Input> inputs) {
	List<InputConfiguration> results = new ArrayList<>();

	for (Input input : inputs) {
	    InputConfiguration config = new InputConfiguration();
	    config.setComparator(buildComparatorConfiguration(input.getRule().getComparator()));
	    config.setType(input.getRule().getMetric().getType());
	    config.setWeight(input.getWeight());

	    results.add(config);
	}

	return results;
    }

    private static ComparatorConfiguration buildComparatorConfiguration(ITradingRuleComparator comparator) {
	ComparatorConfiguration result = new ComparatorConfiguration();
	if (comparator instanceof BinaryComparator) {
	    result.setBinary(true);
	    result.setThreshold1(((BinaryComparator) comparator).getThreshold());
	    result.setType(((BinaryComparator) comparator).getType());
	} else if (comparator instanceof RangeComparator) {
	    result.setBinary(false);
	    result.setThreshold1(((RangeComparator) comparator).getBegin());
	    result.setThreshold1(((RangeComparator) comparator).getEnd());
	}
	return result;
    }
}
