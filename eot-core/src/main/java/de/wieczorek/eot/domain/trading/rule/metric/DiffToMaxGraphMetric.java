package de.wieczorek.eot.domain.trading.rule.metric;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class DiffToMaxGraphMetric extends AbstractGraphMetric {
    public DiffToMaxGraphMetric() {
	setType(GraphMetricType.DiffToMax);
    }

    @Override
    protected double calculateRatingCPU(ExchangeRateHistory history) {

	List<TimedExchangeRate> historyData = history.getCompleteHistoryData();
	double max = historyData.parallelStream().mapToDouble(t -> t.getToPrice()).max().getAsDouble();
	double current = history.getMostRecentExchangeRate().getToPrice();

	return (max - current) / ((max + current) / 2) * 100;
    }

}
