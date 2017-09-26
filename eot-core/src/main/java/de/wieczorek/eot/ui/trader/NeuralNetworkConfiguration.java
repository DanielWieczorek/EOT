package de.wieczorek.eot.ui.trader;

import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork.NetworkType;

public class NeuralNetworkConfiguration {
    private NetworkType type;
    private TradingRulePerceptronConfiguration perceptron1;
    private TradingRulePerceptronConfiguration perceptron2;
}
