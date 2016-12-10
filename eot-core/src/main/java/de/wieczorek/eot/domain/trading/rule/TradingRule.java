package de.wieczorek.eot.domain.trading.rule;

import java.util.HashMap;
import java.util.Map;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.GraphMetricType;

public class TradingRule {

    private double threshold;
    private ComparatorType comparator;

    private AbstractGraphMetric metric;
    private static Map<GraphMetricType, Tuple<TimedExchangeRate, Double>> ratingCache = new HashMap<>();

    public boolean evaluate(ExchangeRateHistory history) {
	double rating = 0.0;
	TimedExchangeRate reference = history.getMostRecentExchangeRate();
	if (ratingCache.containsKey(metric.getType())
		&& ratingCache.get(metric.getType()).equals(new Tuple<>(reference, 0.0))) {
	    rating = ratingCache.get(metric.getType()).y;
	} else {
	    rating = metric.getRating(history);
	    ratingCache.put(metric.getType(), new Tuple<>(reference, rating));
	}

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

    public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
	    this.x = x;
	    this.y = y;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((x == null) ? 0 : x.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Tuple other = (Tuple) obj;
	    if (x == null) {
		if (other.x != null)
		    return false;
	    } else if (!x.equals(other.x))
		return false;
	    return true;
	}

	private TradingRule getOuterType() {
	    return TradingRule.this;
	}

    }
}
