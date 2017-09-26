package de.wieczorek.eot.domain.trading.rule.comparator;

import java.util.List;

import org.apache.log4j.Logger;

import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;

public class BinaryComparator implements ITradingRuleComparator {

    private static final Logger LOGGER = Logger.getLogger(TradingRulePerceptron.class.getName());

    private double threshold;
    private ComparatorType type;

    public BinaryComparator(double threshold, ComparatorType type) {
	this.threshold = threshold;
	this.type = type;
    }

    @Override
    public boolean apply(double value) {
	boolean result = false;
	switch (type) {
	case GREATER:
	    result = value > threshold;
	    break;
	case LESS:
	    result = value < threshold;
	    break;
	default:
	    result = false;
	}

	LOGGER.info(value + " " + type.name() + " " + threshold + " = " + result);

	return result;
    }

    @Override
    public ITradingRuleComparator combineWith(List<ITradingRuleComparator> comparators) {
	double sumOfBinaryComparators = comparators.stream().filter(c -> c instanceof BinaryComparator)
		.mapToDouble(c -> ((BinaryComparator) c).threshold).sum();
	long count = comparators.stream().filter(c -> c instanceof BinaryComparator).count();
	return new BinaryComparator((sumOfBinaryComparators + threshold) / (count + 1), type);
    }

    @Override
    public String printDescription() {

	return type + "_" + threshold;
    }

}
