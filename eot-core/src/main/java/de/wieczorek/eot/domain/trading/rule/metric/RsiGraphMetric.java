package de.wieczorek.eot.domain.trading.rule.metric;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class RsiGraphMetric extends AbstractGraphMetric {

    public RsiGraphMetric() {
	this.type = GraphMetricType.RSI;
    }

    // private final int MULTIPLICATOR = 15;

    @Override
    public double getRating(ExchangeRateHistory history) {
	ExchangeRateHistory input = history;

	double averageGain = 0.0;
	double averageLoss = 0.0;

	for (int i = 1; i < 15; i += 1) {
	    TimedExchangeRate lastDatapoint = input.getCompleteHistoryData().get(i - 1);
	    TimedExchangeRate currentDatapoint = input.getCompleteHistoryData().get(i);

	    if (lastDatapoint.getToPrice() > currentDatapoint.getToPrice())
		averageLoss += lastDatapoint.getToPrice() - currentDatapoint.getToPrice();
	    else
		averageGain += currentDatapoint.getToPrice() - lastDatapoint.getToPrice();
	}
	averageGain /= 14;
	averageLoss /= 14;

	for (int i = 16; i < input.getCompleteHistoryData().size(); i += 1) {
	    TimedExchangeRate lastDatapoint = input.getCompleteHistoryData().get(i - 1);
	    TimedExchangeRate currentDatapoint = input.getCompleteHistoryData().get(i);

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
