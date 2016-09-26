package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangeRateHistory;

public class TradingRule {

	private double threshold;
	private ComparatorType comparator;

	private AbstractGraphMetric metric;

	public boolean evaluate(ExchangeRateHistory history) {

		switch (getComparator()) {
		case EQUAL:
			return metric.getRating(history) == threshold;
		case GREATER:
			return metric.getRating(history) > threshold;
		case LESS:
			return metric.getRating(history) < threshold;
		default:
			return false;
		}
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public AbstractGraphMetric getMetric() {
		return metric;
	}

	public void setMetric(AbstractGraphMetric metric) {
		this.metric = metric;
	}

	public ComparatorType getComparator() {
		return comparator;
	}

	public void setComparator(ComparatorType comparator) {
		this.comparator = comparator;
	}
}
