package de.wieczorek.eot.domain.trading.rule.metric;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class MacdGraphMetric extends AbstractGraphMetric {

    public MacdGraphMetric() {
	this.setType(GraphMetricType.MACD);
    }

    @Override
    protected final double calculateRatingCPU(final ExchangeRateHistory history) {
	List<TimedExchangeRate> data = history.getCompleteHistoryData();
	final int averageRange = data.size() / 6;
	int sizeOfBiggerRange = data.size() - 1 - averageRange;
	int sizeOfSmallerRange = data.size() / 3 - averageRange - 1;

	int biggerRangeStartingPosition = data.size() - sizeOfBiggerRange - 1;
	int smallerRangeStartingPosition = data.size() - sizeOfSmallerRange - 1;

	double average = 0.0;
	for (int i = 0; i < averageRange; i++) {
	    average += data.get(i).getToPrice();
	}
	average /= averageRange;

	double smallerMovingAverage = average;
	double weightingFactor = 2.0 / (sizeOfSmallerRange + 1);
	for (int i = smallerRangeStartingPosition; i < smallerRangeStartingPosition + sizeOfSmallerRange; i++) {

	    smallerMovingAverage = ((data.get(i).getToPrice() - smallerMovingAverage) * weightingFactor)
		    + smallerMovingAverage;
	}

	double biggerMovingAverage = average;
	weightingFactor = 2.0 / (sizeOfBiggerRange + 1);
	for (int i = biggerRangeStartingPosition; i < biggerRangeStartingPosition + sizeOfBiggerRange; i++) {
	    biggerMovingAverage = ((data.get(i).getToPrice() - biggerMovingAverage) * weightingFactor)
		    + biggerMovingAverage;
	}

	return (smallerMovingAverage - biggerMovingAverage) * 10000;
    }

}
