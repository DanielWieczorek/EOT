package de.wieczorek.eot.domain.trading.rule.metric;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class StochasticFastGraphMetric extends AbstractGraphMetric {

    public StochasticFastGraphMetric() {
	this.type = GraphMetricType.StochasticFast;
    }

    @Override
    protected final double calculateRatingCPU(final ExchangeRateHistory history) {
	double minPrice = Double.MAX_VALUE;
	double maxPrice = Double.MIN_VALUE;
	double priceRange = Double.MIN_NORMAL;

	List<TimedExchangeRate> historyData = history.getCompleteHistoryData();
	for (TimedExchangeRate rate : historyData) {
	    if (rate.getToPrice() < minPrice) {
		minPrice = rate.getToPrice();
	    }
	    if (rate.getToPrice() > maxPrice) {
		maxPrice = rate.getToPrice();
	    }
	}

	priceRange = maxPrice - minPrice;

	double result = history.getMostRecentExchangeRate().getToPrice();
	result -= minPrice;
	result /= priceRange;
	result *= 100;
	return result;
    }

}
