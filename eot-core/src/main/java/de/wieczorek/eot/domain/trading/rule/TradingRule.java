package de.wieczorek.eot.domain.trading.rule;

import java.util.HashMap;
import java.util.Map;

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
    private static volatile Map<Tuple<TimedExchangeRate, GraphMetricType>, Double> ratingCache = new HashMap<>();

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

	if (ratingCache.size() > 100) {
	    ratingCache.clear();
	    ratingValueCache.clear();
	}

	if (ratingCache.containsKey(new Tuple<TimedExchangeRate, GraphMetricType>(reference, metric.getType()))) {
	    rating = ratingCache.get(new Tuple<TimedExchangeRate, GraphMetricType>(reference, metric.getType()));
	} else {

	    rating = metric.getRating(history);

	    if (!ratingValueCache.containsKey(rating)) {
		ratingValueCache.put(rating, rating);
	    }
	    // System.out.println("Adding new entry to TradingRule: reference:"
	    // + reference.getTime().toString().toString()
	    // + " " + reference.getFrom() + "" + reference.getTo() + " metric:
	    // " + metric.getType());
	    Double ratingReference = ratingValueCache.get(rating);
	    ratingCache.put(new Tuple<>(reference, metric.getType()), ratingReference);
	}

	lastReference = reference;
	if (rating == null || Double.isNaN(rating))
	    return false;

	switch (getComparator()) {
	case EQUAL:
	    return rating == threshold;
	case GREATER:
	    return rating > threshold;
	case LESS:
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
    public class Tuple<X, Y> {
	/**
	 * Fist component of the tuple.
	 */
	private final X x;
	/**
	 * Second component of the tuple.
	 */
	private final Y y;

	/**
	 * Constructor.
	 * 
	 * @param xInput
	 *            Fist component of the tuple.
	 * @param yInput
	 *            Second component of the tuple.
	 */
	public Tuple(final X xInput, final Y yInput) {
	    this.x = xInput;
	    this.y = yInput;
	}

	@Override
	public final int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getOuterType().hashCode();
	    result = prime * result + ((x == null) ? 0 : x.hashCode());
	    result = prime * result + ((y == null) ? 0 : y.hashCode());
	    return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public final boolean equals(final Object obj) {
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
	    return true;
	}

	private TradingRule getOuterType() {
	    return TradingRule.this;
	}

    }
}
