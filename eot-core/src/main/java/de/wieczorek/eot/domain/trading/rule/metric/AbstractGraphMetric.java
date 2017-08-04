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
    private GraphMetricType type;

    /**
     * 
     * @param history
     * @return
     */
    public final double getRating(final ExchangeRateHistory history) {
	return calculateRatingCPU(history);
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
	throw new UnsupportedOperationException("Calculation via GPU is not enabled for " + getType().name());
    }

    public final GraphMetricType getType() {
	return type;
    }

    public void setType(GraphMetricType type) {
	this.type = type;
    }

}
