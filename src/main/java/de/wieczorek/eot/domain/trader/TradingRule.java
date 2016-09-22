package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangeRateHistory;

public class TradingRule {

	private OrderType type;
	private double threshold;

	private AbstractGraphMetric metric;

	public boolean evaluate(ExchangeRateHistory history) {
		if (type.equals(OrderType.BUY))
			return metric.getRating(history) > threshold;
		else
			return metric.getRating(history) < threshold;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
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
}
