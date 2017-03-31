package de.wieczorek.eot.domain.trading.rule.metric;

import java.util.List;

import com.aparapi.Kernel;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class StochasticFastGraphMetric extends AbstractGraphMetric {

    private static Kernel kernel1;

    public StochasticFastGraphMetric() {
	this.type = GraphMetricType.StochasticFast;
	this.strategy = ExecutionLocationStrategy.GPU_ONLY;
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

    @Override
    protected final double calculateRatingGPU(final ExchangeRateHistory history) {
	double[] minPrice = new double[] { Double.MAX_VALUE };
	double[] maxPrice = new double[] { Double.MIN_VALUE };
	double[] priceRange = new double[] { Double.MIN_NORMAL };

	if (kernel1 == null) {
	    kernel1 = new Kernel() {
		@Override
		public void run() {
		    int i = getGlobalId();

		    List<TimedExchangeRate> historyData = history.getCompleteHistoryData();
		    TimedExchangeRate rate = historyData.get(i);
		    if (rate.getToPrice() < minPrice[0]) {
			minPrice[0] = rate.getToPrice();
		    }
		    if (rate.getToPrice() > maxPrice[0]) {
			maxPrice[0] = rate.getToPrice();
		    }
		}

	    };
	}

	priceRange[0] = maxPrice[0] - minPrice[0];

	double result = history.getMostRecentExchangeRate().getToPrice();
	result -= minPrice[0];
	result /= priceRange[0];
	result *= 100;
	return result;
    }

}
