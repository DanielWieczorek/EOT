package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;

public class TradingRule {

	private OrderType type;
	private double threshold;
	private ExchangableType fromExchangable;
	private ExchangableType toExchangable;
	
	private AbstractGraphMetric metric;
	
	public boolean evaluate(ExchangeRateHistory history){
		return metric.getRating(history) > threshold;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public ExchangableType getFromExchangable() {
		return fromExchangable;
	}

	public void setFromExchangable(ExchangableType fromExchangable) {
		this.fromExchangable = fromExchangable;
	}

	public ExchangableType getToExchangable() {
		return toExchangable;
	}

	public void setToExchangable(ExchangableType toExchangable) {
		this.toExchangable = toExchangable;
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
