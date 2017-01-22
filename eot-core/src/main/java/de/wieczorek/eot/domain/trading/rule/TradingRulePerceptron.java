package de.wieczorek.eot.domain.trading.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

/**
 * Perceptron combining multiple trading rules.
 * 
 * @author Daniel Wieczorek
 *
 */
public class TradingRulePerceptron {

    /**
     * List of weighted Trading rules.
     */
    private List<Input> inputs;
    /**
     * Threshold when the perceptron fires.
     */
    private double threshold;

    /**
     * Constructor.
     * 
     * @param thresholdInput
     *            threshold when the perceptron activates.
     */
    public TradingRulePerceptron(final double thresholdInput) {
	setInputs(new LinkedList<>());
	this.setThreshold(thresholdInput);
    }

    /**
     * Constructor.
     * 
     * @param rule
     *            trading rule to add.
     * @param weight
     *            Weight of the trading rule
     * @param thresholdInput
     *            threshold when the perceptron activates.
     */
    public TradingRulePerceptron(final TradingRule rule, final double weight, final double thresholdInput) {
	this(thresholdInput);
	getInputs().add(new Input(rule, weight));
    }

    /**
     * Checks if the perceptron activates based on the given input.
     * 
     * @param history
     *            history which is needed for the calculations of the graph
     *            metrics.
     * @return true if the perceptron activates, false else.
     */
    public final boolean isActivated(final ExchangeRateHistory history) {

	double sumOfInputs = 0.0;
	for (Input input : getInputs()) {
	    if (input.getRule().evaluate(history)) {
		sumOfInputs += input.getWeight();
	    }
	}

	return sumOfInputs >= getThreshold();
    }

    /**
     * adds a trading rule as input.
     * 
     * @param rule
     *            the rule to add
     * @param weight
     *            the weight the input has.
     */
    public final void add(final TradingRule rule, final double weight) {
	getInputs().add(new Input(rule, weight));
    }

    /**
     * Class which combines a trading rule with a weight which is used for this
     * perceptron.
     * 
     * @author Daniel Wieczorek
     *
     */
    public class Input {
	/**
	 * The trading rule of which the result serves as input.
	 */
	private TradingRule rule;

	/**
	 * The weight which determines how much it influences the result of the
	 * perceptron.
	 */
	private double weight;

	/**
	 * Constructor.
	 * 
	 * @param ruleInput
	 *            the rule which serves as input
	 * @param weightInput
	 *            the weight of the input.
	 */
	public Input(final TradingRule ruleInput, final double weightInput) {
	    this.setRule(ruleInput);
	    this.setWeight(weightInput);
	}

	public final TradingRule getRule() {
	    return rule;
	}

	public final void setRule(final TradingRule ruleInput) {
	    this.rule = ruleInput;
	}

	public final double getWeight() {
	    return weight;
	}

	public final void setWeight(final double weightInput) {
	    this.weight = weightInput;
	}
    }

    /**
     * Combines two percentrons with each other. This is needed for the
     * combination of two traders.
     * 
     * @param p2
     *            perceptron to combine this one with.
     * @return a new instance which represents the combined result.
     */
    public final TradingRulePerceptron combineWith(final TradingRulePerceptron p2) {
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

    /**
     * Combines the given inputs which must have the same type of trading rule
     * as input. The weight will be the sum of the two inputs. Threshold of the
     * rule will be the average of the rules combined.
     * 
     * @param inputsToCombine
     *            inputs to combine.
     * @return a new input representing the combination of those two inputs.
     */
    private Input combineInputs(final Collection<Input> inputsToCombine) {
	TradingRule t = new TradingRule();
	Input first = inputsToCombine.iterator().next();
	t.setComparator(first.rule.getComparator());
	t.setMetric(first.rule.getMetric());
	t.setThreshold(
		inputsToCombine.stream().mapToDouble(p -> p.getRule().getThreshold()).sum() / inputsToCombine.size());

	return new Input(t, inputsToCombine.stream().mapToDouble(p -> p.weight).sum());

    }

    /**
     * Randomizes the threshold of of the perceptron.
     */
    public final void setRandomThreshold() {
	double maxValue = getInputs().stream().mapToDouble(p -> p.getWeight()).sum();
	Random r = new Random(System.currentTimeMillis());
	boolean useMax = r.nextBoolean();
	if (useMax) {
	    setThreshold(maxValue);
	} else {
	    double weightToUseAsMax = getInputs().get(r.nextInt(getInputs().size())).getWeight();
	    setThreshold(weightToUseAsMax);
	}
    }

    public final List<Input> getInputs() {
	return inputs;
    }

    public final void setInputs(final List<Input> inputList) {
	this.inputs = inputList;
    }

    public final double getThreshold() {
	return threshold;
    }

    public final void setThreshold(final double thresholdInput) {
	this.threshold = thresholdInput;
    }
}
