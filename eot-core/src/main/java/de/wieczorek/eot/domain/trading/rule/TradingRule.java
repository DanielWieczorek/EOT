package de.wieczorek.eot.domain.trading.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
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

    private TimedExchangeRate lastReference;

    /**
     * The cache for the results from the graph metric.
     */
    private static volatile Map<Tuple<TimedExchangeRate, Integer, GraphMetricType>, Double> ratingCache = new HashMap<>();

    private static volatile Map<Double, Double> ratingValueCache = new HashMap<>();

    /**
     * Determines whether a trade should be performed.
     * 
     * @param history
     *            data the decision is based upon.
     * @return true if a trade should be performed.
     */
    public final boolean evaluate(final ExchangeRateHistory history) {
	Double rating = 0.0;
	TimedExchangeRate reference = history.getMostRecentExchangeRate();

	if (ratingCache.size() > 1) {
	    ratingCache.clear();
	    ratingValueCache.clear();
	}

	if (ratingCache.containsKey(new Tuple<TimedExchangeRate, Integer, GraphMetricType>(reference,
		history.getCompleteHistoryData().size(), metric.getType()))) {
	    rating = ratingCache.get(new Tuple<TimedExchangeRate, Integer, GraphMetricType>(reference,
		    history.getCompleteHistoryData().size(), metric.getType()));
	} else {

	    rating = metric.getRating(history);

	    if (!ratingValueCache.containsKey(rating)) {
		ratingValueCache.put(rating, rating);
	    }
	    Double ratingReference = ratingValueCache.get(rating);
	    ratingCache.put(new Tuple<>(reference, history.getCompleteHistoryData().size(), metric.getType()),
		    ratingReference);
	}

	lastReference = reference;
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

    /**
     * Class representing a two-tuple.
     * 
     * @author Daniel Wieczorek
     *
     * @param <X>
     *            type of first component of the tuple
     * @param <Y>
     *            type of second component of the tuple
     */
    public class Tuple<X, Y, Z> {
	/**
	 * Fist component of the tuple.
	 */
	private final X x;
	/**
	 * Second component of the tuple.
	 */
	private final Y y;

	/**
	 * Second component of the tuple.
	 */
	private final Z z;

	/**
	 * Constructor.
	 * 
	 * @param xInput
	 *            Fist component of the tuple.
	 * @param yInput
	 *            Second component of the tuple.
	 */
	public Tuple(final X xInput, final Y yInput, final Z zInput) {
	    this.x = xInput;
	    this.y = yInput;
	    this.z = zInput;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((x == null) ? 0 : x.hashCode());
	    result = prime * result + ((y == null) ? 0 : y.hashCode());
	    result = prime * result + ((z == null) ? 0 : z.hashCode());
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
	    Tuple other = (Tuple) obj;
	    if (x == null) {
		if (other.x != null) {
		    return false;
		}
	    } else if (!x.equals(other.x)) {
		return false;
	    }
	    if (y == null) {
		if (other.y != null) {
		    return false;
		}
	    } else if (!y.equals(other.y)) {
		return false;
	    }
	    if (z == null) {
		if (other.z != null) {
		    return false;
		}
	    } else if (!z.equals(other.z)) {
		return false;
	    }
	    return true;
	}
    }
}
