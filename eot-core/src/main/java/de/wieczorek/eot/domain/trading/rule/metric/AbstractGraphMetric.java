package de.wieczorek.eot.domain.trading.rule.metric;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public abstract class AbstractGraphMetric {
    protected ExchangeRateHistory data;
    protected GraphMetricType type;
    protected boolean isGpuEnabled;
    private boolean wasGpuUsedLast;

    public double getRating(ExchangeRateHistory history) {
	if (isGpuEnabled) {
	    if (wasGpuUsedLast) {
		wasGpuUsedLast = false;
		return calculateRatingCPU(history);
	    } else {
		wasGpuUsedLast = true;
		return calculateRatingGPU(history);
	    }
	} else {
	    return calculateRatingCPU(history);
	}
    }

    protected abstract double calculateRatingCPU(ExchangeRateHistory history);

    protected double calculateRatingGPU(ExchangeRateHistory history) {
	throw new UnsupportedOperationException("Calculation via GPU is not enabled for " + type.name());
    }

    public GraphMetricType getType() {
	return type;
    }

}
