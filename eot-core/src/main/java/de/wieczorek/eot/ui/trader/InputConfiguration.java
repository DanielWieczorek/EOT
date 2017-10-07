package de.wieczorek.eot.ui.trader;

import de.wieczorek.eot.domain.trading.rule.metric.GraphMetricType;

public class InputConfiguration {
    private double weight;

    private ComparatorConfiguration comparator;

    private GraphMetricType type;

    public double getWeight() {
	return weight;
    }

    public void setWeight(double weight) {
	this.weight = weight;
    }

    public ComparatorConfiguration getComparator() {
	return comparator;
    }

    public void setComparator(ComparatorConfiguration comparator) {
	this.comparator = comparator;
    }

    public GraphMetricType getType() {
	return type;
    }

    public void setType(GraphMetricType type) {
	this.type = type;
    }

}
