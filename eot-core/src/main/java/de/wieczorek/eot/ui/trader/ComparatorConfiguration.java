package de.wieczorek.eot.ui.trader;

import de.wieczorek.eot.domain.trading.rule.comparator.ComparatorType;

public class ComparatorConfiguration {
    boolean isBinary;

    private double threshold1;
    private double threshold2;

    private ComparatorType type;

    public boolean isBinary() {
	return isBinary;
    }

    public void setBinary(boolean isBinary) {
	this.isBinary = isBinary;
    }

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

    public ComparatorType getType() {
	return type;
    }

    public void setType(ComparatorType type) {
	this.type = type;
    }

}
