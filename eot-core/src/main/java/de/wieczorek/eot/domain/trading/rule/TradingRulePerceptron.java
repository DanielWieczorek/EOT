package de.wieczorek.eot.domain.trading.rule;

import java.util.LinkedList;
import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public class TradingRulePerceptron {

    private List<Input> inputs;
    private double threshold;

    public TradingRulePerceptron(double threshold) {
	inputs = new LinkedList<>();
	this.threshold = threshold;
    }

    public TradingRulePerceptron(TradingRule rule, double weight, double threshold) {
	this(threshold);
	inputs.add(new Input(rule, weight));
    }

    public boolean isActivated(ExchangeRateHistory history) {

	double sumOfInputs = 0.0;
	for (Input input : inputs) {
	    if (input.getRule().evaluate(history))
		sumOfInputs += input.getWeight();
	}

	return sumOfInputs >= threshold;
    }

    public void add(TradingRule rule, double weight) {
	inputs.add(new Input(rule, weight));
    }

    private class Input {
	private TradingRule rule;
	private double weight;

	public Input(TradingRule rule, double weight) {
	    this.setRule(rule);
	    this.setWeight(weight);
	}

	public TradingRule getRule() {
	    return rule;
	}

	public void setRule(TradingRule rule) {
	    this.rule = rule;
	}

	public double getWeight() {
	    return weight;
	}

	public void setWeight(double weight) {
	    this.weight = weight;
	}
    }

    public TradingRulePerceptron combineWith(TradingRulePerceptron p2) {

	return null;
    }
}
