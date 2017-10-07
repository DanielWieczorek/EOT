package de.wieczorek.eot.domain.trading.rule.comparator;

import java.util.List;

public class RangeComparator implements ITradingRuleComparator {

    private double begin;
    private double end;

    public RangeComparator(double begin, double end) {
	this.begin = begin;
	this.end = end;
    }

    @Override
    public boolean apply(double value) {

	return begin <= value && value >= end;
    }

    @Override
    public ITradingRuleComparator combineWith(List<ITradingRuleComparator> comparators) {
	double sumOfBinaryComparatorsBegin = comparators.stream().filter(c -> c instanceof RangeComparator)
		.mapToDouble(c -> ((RangeComparator) c).begin).sum();
	double sumOfBinaryComparatorsEnd = comparators.stream().filter(c -> c instanceof RangeComparator)
		.mapToDouble(c -> ((RangeComparator) c).end).sum();
	long count = comparators.stream().filter(c -> c instanceof BinaryComparator).count();
	return new RangeComparator((sumOfBinaryComparatorsBegin + begin) / (count + 1),
		(sumOfBinaryComparatorsEnd + end) / (count + 1));
    }

    @Override
    public String printDescription() {
	return "BETWEEN_" + begin + "_" + end;
    }

    public double getBegin() {
	return begin;
    }

    public void setBegin(double begin) {
	this.begin = begin;
    }

    public double getEnd() {
	return end;
    }

    public void setEnd(double end) {
	this.end = end;
    }
}
