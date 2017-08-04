package de.wieczorek.eot.domain.trading.rule.metric;

import java.util.List;

import com.aparapi.Kernel;
import com.aparapi.Range;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class RsiGraphMetric extends AbstractGraphMetric {

    private static Kernel kernel1;
    private static Range range;

    public RsiGraphMetric() {
	this.setType(GraphMetricType.RSI);
    }

    @Override
    protected double calculateRatingCPU(ExchangeRateHistory history) {
	ExchangeRateHistory input = history;

	double averageGain = 0.0;
	double averageLoss = 0.0;

	List<TimedExchangeRate> historyData = input.getCompleteHistoryData();
	int historySize = historyData.size();
	int numberDataPointsForInitialAverage = Math.min(15, historySize / 10);

	for (int i = 1; i < numberDataPointsForInitialAverage; i += 1) {
	    TimedExchangeRate lastDatapoint = historyData.get(i - 1);
	    TimedExchangeRate currentDatapoint = historyData.get(i);

	    if (lastDatapoint.getToPrice() > currentDatapoint.getToPrice())
		averageLoss += lastDatapoint.getToPrice() - currentDatapoint.getToPrice();
	    else
		averageGain += currentDatapoint.getToPrice() - lastDatapoint.getToPrice();
	}

	averageGain /= numberDataPointsForInitialAverage;
	averageLoss /= numberDataPointsForInitialAverage;

	for (int i = numberDataPointsForInitialAverage + 1; i < historySize; i += 1) {
	    TimedExchangeRate lastDatapoint = historyData.get(i - 1);
	    TimedExchangeRate currentDatapoint = historyData.get(i);

	    if (lastDatapoint.getToPrice() < currentDatapoint.getToPrice())
		averageGain = (averageGain * 13.0 + currentDatapoint.getToPrice()) / 14.0;
	    else
		averageLoss = (averageLoss * 13.0 + currentDatapoint.getToPrice()) / 14.0;
	}

	double relativeStrength = averageGain / averageLoss;

	// System.out.println("Relative Strength: " + (100.0 - (100.0 / (1.0 +
	// relativeStrength))));

	return 100.0 - (100.0 / (1.0 + relativeStrength));
    }
}
