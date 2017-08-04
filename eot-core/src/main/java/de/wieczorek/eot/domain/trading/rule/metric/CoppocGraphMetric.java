package de.wieczorek.eot.domain.trading.rule.metric;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class CoppocGraphMetric extends AbstractGraphMetric {
    private final int MULTIPLICATOR = 15;

    public CoppocGraphMetric() {
	setType(GraphMetricType.Coppoch);
    }

    @Override
    public double calculateRatingCPU(ExchangeRateHistory history) {

	ExchangeRateHistory input = history;
	ExchangeRateHistory racOutput = new ExchangeRateHistory();
	int adaptiveMultiplicator = (input.getCompleteHistoryData().size() - 10) / 15;

	for (int i = input.getCompleteHistoryData().size() - 10; i < input.getCompleteHistoryData().size(); i += 1) {
	    TimedExchangeRate currentDatapoint = input.getCompleteHistoryData().get(i);

	    double roc11 = ((currentDatapoint.getToPrice()
		    - input.getCompleteHistoryData().get(i - 11 * adaptiveMultiplicator).getToPrice())
		    / input.getCompleteHistoryData().get(i - 11 * adaptiveMultiplicator).getToPrice()) * 100;

	    double roc14 = ((currentDatapoint.getToPrice()
		    - input.getCompleteHistoryData().get(i - 14 * adaptiveMultiplicator).getToPrice())
		    / input.getCompleteHistoryData().get(i - 14 * adaptiveMultiplicator).getToPrice()) * 100;

	    racOutput.add(new TimedExchangeRate(currentDatapoint.getFrom(), currentDatapoint.getTo(), roc11 + roc14,
		    currentDatapoint.getTime()));
	}

	TimedExchangeRate currentDatapoint = racOutput.getCompleteHistoryData()
		.get(racOutput.getCompleteHistoryData().size() - 1);
	ExchangeRateHistory weightedAverageSet = racOutput.getHistoryEntriesBefore(currentDatapoint.getTime(), 10);

	double average = 0;

	for (int j = 0; j < weightedAverageSet.getCompleteHistoryData().size(); j++) {
	    average += weightedAverageSet.getCompleteHistoryData().get(j).getToPrice() * (j + 1.0);
	}
	// System.out.println(weightedAverageSet.getCompleteHistoryData().size());
	average = average / (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10);
	return average;
    }
}
