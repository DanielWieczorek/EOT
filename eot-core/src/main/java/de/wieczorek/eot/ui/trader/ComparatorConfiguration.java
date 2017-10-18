package de.wieczorek.eot.ui.trader;

import de.wieczorek.eot.domain.trading.rule.comparator.BinaryComparatorType;
import de.wieczorek.eot.domain.trading.rule.comparator.ChangeComparatorType;

public class ComparatorConfiguration {
    private ComparatorConfigurationType comparator;

    private double threshold1;
    private double threshold2;

    private BinaryComparatorType binaryType;

    private ChangeComparatorType changeType;

    public double getThreshold1() {
	return threshold1;
    }

    public void setThreshold1(double threshold1) {
	this.threshold1 = threshold1;
    }

    public double getThreshold2() {
	return threshold2;
    }

    public void setThreshold2(double threshold2) {
	this.threshold2 = threshold2;
    }

    public BinaryComparatorType getBinaryType() {
	return binaryType;
    }

    public void setBinaryType(BinaryComparatorType type) {
	this.binaryType = type;
    }

    public ChangeComparatorType getChangeType() {
	return changeType;
    }

    public void setChangeType(ChangeComparatorType changeType) {
	this.changeType = changeType;
    }

    public ComparatorConfigurationType getComparator() {
	return comparator;
    }

    public void setComparator(ComparatorConfigurationType comparator) {
	this.comparator = comparator;
    }

}
