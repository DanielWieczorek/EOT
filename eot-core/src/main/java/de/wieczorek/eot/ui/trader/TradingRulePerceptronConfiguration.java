package de.wieczorek.eot.ui.trader;

import java.util.List;

public class TradingRulePerceptronConfiguration {

    private List<InputConfiguration> inputs;

    private double threshold;

    private int observationTime;

    public List<InputConfiguration> getInputs() {
	return inputs;
    }

    public void setInputs(List<InputConfiguration> inputs) {
	this.inputs = inputs;
    }

    public double getThreshold() {
	return threshold;
    }

    public void setThreshold(double threshold) {
	this.threshold = threshold;
    }

    public int getObservationTime() {
	return observationTime;
    }

    public void setObservationTime(int observationTime) {
	this.observationTime = observationTime;
    }

}
