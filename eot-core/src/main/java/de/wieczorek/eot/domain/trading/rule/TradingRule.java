package de.wieczorek.eot.domain.trading.rule;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.GraphMetricType;

/**
 * A trading rule which is used by a {@link Trader} to determine whether a trade
 * should be generated. This trading rule uses an {@link AbstractGraphMetric}
 * and compares the result to a given threshold. If this condition is fulfilled
 * then this rule permits the trade.
 * 
 * @author Daniel Wieczorek
 *
 */
public class TradingRule {
    private static final Logger LOGGER = Logger.getLogger(TradingRule.class.getName());
    /**
     * Threshold it is compared against.
     */
    private double threshold = 0.0;

    /**
     * Comparator which is compared against.
     */
    private ComparatorType comparator;

    /**
     * The metric which computes the current value depending on the current
     * graph.
     */
    private AbstractGraphMetric metric;

    private static final int MAX_CACHE_SIZE = 64;

    private static volatile Map<CalculationResultKey, Double> cache = new ConcurrentHashMap<>();
    private static volatile Queue<CalculationResultKey> cacheList = new ConcurrentLinkedQueue<>();

    /**
     * Determines whether a trade should be performed.
     * 
     * @param history
     *            data the decision is based upon.
     * @return true if a trade should be performed.
     */
    public final boolean evaluate(final ExchangeRateHistory history) {
	Double rating = 0.0;
	// TimedExchangeRate reference = history.getMostRecentExchangeRate();

	CalculationResultKey key = new CalculationResultKey(
		history.getMostRecentExchangeRate().getTime().toEpochSecond(ZoneOffset.UTC), metric.getType(),
		history.getCompleteHistoryData().size());

	rating = cache.get(key);
	if (rating == null) {
	    rating = metric.getRating(history);

	    cache.put(key, rating);
	    cacheList.add(key);

	    int superflousItems = cache.size() - MAX_CACHE_SIZE;

	    for (int i = 0; i < superflousItems; i++) {
		if (!cacheList.isEmpty()) {
		    cache.remove(cacheList.poll());
		}
	    }
	}

	if (rating == null || Double.isNaN(rating)) {
	    return false;
	}

	switch (getComparator()) {
	case EQUAL:
	    LOGGER.fine("rating (" + rating + ") == threshold (" + threshold + ")? " + (rating == threshold));
	    return rating == threshold;
	case GREATER:
	    LOGGER.fine("rating (" + rating + ") > threshold (" + threshold + ")? " + (rating > threshold));
	    return rating > threshold;
	case LESS:
	    LOGGER.fine("rating (" + rating + ") < threshold (" + threshold + ")? " + (rating < threshold));
	    return rating < threshold;
	default:
	    return false;
	}
    }

    public final double getThreshold() {
	return threshold;
    }

    public final void setThreshold(final double thresholdInput) {
	this.threshold = thresholdInput;
    }

    public final AbstractGraphMetric getMetric() {
	return metric;
    }

    public final void setMetric(final AbstractGraphMetric metricInput) {
	this.metric = metricInput;
    }

    public final ComparatorType getComparator() {
	return comparator;
    }

    public final void setComparator(final ComparatorType comparatorInput) {
	this.comparator = comparatorInput;
    }

    public class CalculationResultKey {

	private long endTimeInMillies;
	private GraphMetricType metric;
	private int historySize;

	public CalculationResultKey(long endTimeInMillies, GraphMetricType metric, int historySize) {
	    super();
	    this.endTimeInMillies = endTimeInMillies;
	    this.metric = metric;
	    this.historySize = historySize;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + (int) (endTimeInMillies ^ (endTimeInMillies >>> 32));
	    result = prime * result + historySize;
	    result = prime * result + ((metric == null) ? 0 : metric.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    CalculationResultKey other = (CalculationResultKey) obj;
	    if (endTimeInMillies != other.endTimeInMillies) {
		return false;
	    }
	    if (historySize != other.historySize) {
		return false;
	    }
	    if (metric != other.metric) {
		return false;
	    }
	    return true;
	}
    }
}
