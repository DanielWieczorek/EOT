package de.wieczorek.eot.domain.trading.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public class TradingRulePerceptron {

    private List<Input> inputs;
    private double threshold;

    public TradingRulePerceptron(double threshold) {
	setInputs(new LinkedList<>());
	this.setThreshold(threshold);
    }

    public TradingRulePerceptron(TradingRule rule, double weight, double threshold) {
	this(threshold);
	getInputs().add(new Input(rule, weight));
    }

    public boolean isActivated(ExchangeRateHistory history) {

	double sumOfInputs = 0.0;
	for (Input input : getInputs()) {
	    if (input.getRule().evaluate(history))
		sumOfInputs += input.getWeight();
	}

	return sumOfInputs >= getThreshold();
    }

    public void add(TradingRule rule, double weight) {
	getInputs().add(new Input(rule, weight));
    }

    public class Input {
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
	TradingRulePerceptron result = new TradingRulePerceptron(0);

	List<Input> fullList = new ArrayList<>();
	fullList.addAll(this.inputs);
	fullList.addAll(p2.getInputs());
	List<Input> newInputs = fullList.stream() //
		.collect(Collectors
			.groupingBy(p -> p.getRule().getMetric().getType().name() + p.getRule().getComparator().name()))//
		.values().stream() //
		.map(this::combineInputs)//
		.collect(Collectors.toList());

	result.setInputs(newInputs);
	result.setThreshold(this.getThreshold() + p2.getThreshold());
	return result;
    }

    private Input combineInputs(Collection<Input> inputs) {
	TradingRule t = new TradingRule();
	Input first = inputs.iterator().next();
	t.setComparator(first.rule.getComparator());
	t.setMetric(first.rule.getMetric());
	t.setThreshold(inputs.stream().mapToDouble(p -> p.getRule().getThreshold()).sum() / inputs.size());

	return new Input(t, inputs.stream().mapToDouble(p -> p.weight).sum());

    }

    public List<Input> getInputs() {
	return inputs;
    }

    public void setInputs(List<Input> inputs) {
	this.inputs = inputs;
    }

    public double getThreshold() {
	return threshold;
    }

    public void setThreshold(double threshold) {
	this.threshold = threshold;
    }
}
