package de.wieczorek.eot.domain.trading.rule;

import java.util.Arrays;
import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public class DefaultNeuralNetworkNode implements INeuralNetworkNode {

    private double threshold;
    private List<NeuralNetworkNodeConnection> inputs;

    public DefaultNeuralNetworkNode(double threshold, NeuralNetworkNodeConnection... inputs) {
	this.threshold = threshold;
	this.inputs = Arrays.asList(inputs);
    }

    @Override
    public boolean isActivated(ExchangeRateHistory history) {
	double sumOfInputs = inputs.stream()
		.mapToDouble(input -> (input.getOrigin().isActivated(history) ? 1.0 : 0.0) * input.getStrength()).sum();
	return sumOfInputs > threshold;
    }

}
