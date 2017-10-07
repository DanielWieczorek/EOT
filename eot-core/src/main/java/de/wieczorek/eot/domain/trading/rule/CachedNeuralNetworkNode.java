package de.wieczorek.eot.domain.trading.rule;

import java.util.Arrays;
import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public class CachedNeuralNetworkNode implements INeuralNetworkNode {

    private double threshold;
    private List<NeuralNetworkNodeConnection> inputs;

    private boolean cachedResult = false;
    private ExchangeRateHistory lastUsedHistory;

    public CachedNeuralNetworkNode(double threshold, NeuralNetworkNodeConnection... inputs) {
	this.threshold = threshold;
	this.inputs = Arrays.asList(inputs);
    }

    @Override
    public boolean isActivated(ExchangeRateHistory history) {
	boolean result = cachedResult;
	if (lastUsedHistory == null || (!history.getMostRecentExchangeRate().getTime()
		.isEqual(lastUsedHistory.getMostRecentExchangeRate().getTime())
		|| history.getCompleteHistoryData().size() != lastUsedHistory.getCompleteHistoryData().size())) {
	    double sumOfInputs = inputs.stream()
		    .mapToDouble(input -> (input.getOrigin().isActivated(history) ? 1.0 : 0.0) * input.getStrength())
		    .sum();
	    result = sumOfInputs > threshold;
	    cachedResult = result;
	}
	lastUsedHistory = history;
	return result;
    }

}
