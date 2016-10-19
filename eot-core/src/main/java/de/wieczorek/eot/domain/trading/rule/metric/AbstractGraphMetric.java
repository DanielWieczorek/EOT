package de.wieczorek.eot.domain.trading.rule.metric;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public abstract class AbstractGraphMetric {
    protected ExchangeRateHistory data;
    protected GraphMetricType type;

    public abstract double getRating(ExchangeRateHistory history);

    public GraphMetricType getType() {
	return type;
    }

}
