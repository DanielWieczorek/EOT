package de.wieczorek.eot.domain.trading.rule.metric;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.trading.rule.TradingRule;

/**
 * Abstract superclass for all indicators.
 * 
 * @author Daniel Wieczorek
 *
 */
public abstract class AbstractGraphMetric {

    /**
     * The data on which the calculation is performed.
     */
    protected ExchangeRateHistory data;

    /**
     * Type of the metric. This is needed for the {@link TradingRule}
     */
    protected GraphMetricType type;

    /**
     * Whether or not the calculation via the GPU is enabled.
     */
    protected boolean isGpuEnabled;

    /**
     * Whether or not the GPU was used for the last calculaton.
     */
    private boolean wasGpuUsedLast;

    /**
     * 
     * @param history
     * @return
     */
    public final double getRating(final ExchangeRateHistory history) {
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

    /**
     * Calculates the rating via the CPU.
     * 
     * @param history
     *            the graph history on which the result is being calculated
     * @return the calculation result
     */
    protected abstract double calculateRatingCPU(ExchangeRateHistory history);

    /**
     * Calculates the rating via the GPU.
     * 
     * @param history
     *            the graph history on which the result is being calculated
     * @return the calculation result
     */
    protected double calculateRatingGPU(final ExchangeRateHistory history) {
	throw new UnsupportedOperationException("Calculation via GPU is not enabled for " + type.name());
    }

    public final GraphMetricType getType() {
	return type;
    }

}
