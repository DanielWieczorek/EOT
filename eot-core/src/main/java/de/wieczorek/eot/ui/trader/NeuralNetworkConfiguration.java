package de.wieczorek.eot.ui.trader;

import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork.NetworkType;

public class NeuralNetworkConfiguration {

    private NetworkType type;
    private TradingRulePerceptronConfiguration perceptron1;
    private TradingRulePerceptronConfiguration perceptron2;

    public NetworkType getType() {
	return type;
    }

    public void setType(NetworkType type) {
	this.type = type;
    }

    public TradingRulePerceptronConfiguration getPerceptron1() {
	return perceptron1;
    }

    public void setPerceptron1(TradingRulePerceptronConfiguration perceptron1) {
	this.perceptron1 = perceptron1;
    }

    public TradingRulePerceptronConfiguration getPerceptron2() {
	return perceptron2;
    }

    public void setPerceptron2(TradingRulePerceptronConfiguration perceptron2) {
	this.perceptron2 = perceptron2;
    }
}
