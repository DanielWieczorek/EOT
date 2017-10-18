package de.wieczorek.eot.domain.trading.rule.comparator;

import java.util.List;

import org.apache.log4j.Logger;

import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;

public class ChangeComparator implements ITradingRuleComparator {

    private static final Logger LOGGER = Logger.getLogger(TradingRulePerceptron.class.getName());

    private double threshold;
    private ChangeComparatorType type;
    private Boolean lastResult;
    private double range;

    public ChangeComparator(double threshold, double range, ChangeComparatorType type) {
	this.threshold = threshold;
	this.type = type;
	this.range = range;
    }

    @Override
    public boolean apply(double value) {
	boolean result = false;
	if (lastResult != null) {
	    switch (type) {
	    case RiseAboveThreshold:
		result = isAboveThreshold(value) != lastResult;
		break;
	    case FallBelowThreshold:
		result = isBelowThreshold(value) != lastResult;
		break;
	    default:
		result = false;
	    }

	}
	updateLastResult(value);
	LOGGER.info(value + " " + type.name() + " " + threshold + " = " + result);

	return result;
    }

    private void updateLastResult(double value) {
	switch (type) {
	case RiseAboveThreshold:
	    lastResult = isAboveThreshold(value);
	    break;
	case FallBelowThreshold:
	    lastResult = isBelowThreshold(value);
	    break;
	default:
	    lastResult = null;
	}

    }

    private boolean isAboveThreshold(double value) {
	return value > threshold + range;
    }

    private boolean isBelowThreshold(double value) {
	return value < threshold - range;
    }

    @Override
    public ITradingRuleComparator combineWith(List<ITradingRuleComparator> comparators) {
	double sumOfChangeComparatorThresholds = comparators.stream().filter(c -> c instanceof ChangeComparator)
		.mapToDouble(c -> ((ChangeComparator) c).threshold).sum();
	double sumOfChangeComparatorRanges = comparators.stream().filter(c -> c instanceof ChangeComparator)
		.mapToDouble(c -> ((ChangeComparator) c).range).sum();
	long count = comparators.stream().filter(c -> c instanceof ChangeComparator).count();
	return new ChangeComparator((sumOfChangeComparatorThresholds + threshold) / (count + 1), //
		(sumOfChangeComparatorRanges + range) / (count + 1), //
		type);
    }

    @Override
    public String printDescription() {

	return type + "_" + threshold;
    }

    public double getThreshold() {
	return threshold;
    }

    public void setThreshold(double threshold) {
	this.threshold = threshold;
    }

    public ChangeComparatorType getType() {
	return type;
    }

    public void setType(ChangeComparatorType type) {
	this.type = type;
    }

    public double getRange() {
	return range;
    }

    public void setRange(double range) {
	this.range = range;
    }

}
