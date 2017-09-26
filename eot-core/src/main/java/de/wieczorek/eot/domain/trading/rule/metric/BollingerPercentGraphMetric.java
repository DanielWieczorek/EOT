package de.wieczorek.eot.domain.trading.rule.metric;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class BollingerPercentGraphMetric extends AbstractGraphMetric {

    public BollingerPercentGraphMetric() {
	setType(GraphMetricType.BollingerPercent);
    }

    @Override
    protected double calculateRatingCPU(ExchangeRateHistory history) {

	List<TimedExchangeRate> historyData = history.getCompleteHistoryData();
	double average = historyData.parallelStream().mapToDouble(t -> t.getToPrice()).sum() / historyData.size();
	double variance = 0.0;

	variance = historyData.parallelStream()
		.mapToDouble(rate -> ((rate.getToPrice() - average) * (rate.getToPrice() - average))).sum();
	variance = variance / (historyData.size() - 1);

	double standardDeviation = Math.sqrt(variance);

	double upperBand = average + standardDeviation * 2;
	double lowerBand = average - standardDeviation * 2;

	return (history.getMostRecentExchangeRate().getToPrice() - lowerBand) / (upperBand - lowerBand) * 100;
    }

}
