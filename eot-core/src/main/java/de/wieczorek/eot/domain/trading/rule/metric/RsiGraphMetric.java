package de.wieczorek.eot.domain.trading.rule.metric;

import com.aparapi.Kernel;
import com.aparapi.Range;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class RsiGraphMetric extends AbstractGraphMetric {

    private static Kernel kernel1;
    private static Range range;

    public RsiGraphMetric() {
	this.type = GraphMetricType.RSI;
	this.strategy = ExecutionLocationStrategy.GPU_ONLY;
    }

    // private final int MULTIPLICATOR = 15;

    @Override
    protected double calculateRatingCPU(ExchangeRateHistory history) {
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

    @Override
    protected double calculateRatingGPU(ExchangeRateHistory history) {
	ExchangeRateHistory input = history;

	float[] averageGain = new float[1];
	float[] averageLoss = new float[1];

	float[] datapoints = new float[input.getCompleteHistoryData().size()];

	for (int i = 0; i < input.getCompleteHistoryData().size(); i++)
	    datapoints[i] = (float) input.getCompleteHistoryData().get(i).getToPrice();

	if (kernel1 == null) {
	    kernel1 = new Kernel() {
		@Override
		public void run() {
		    int i = getGlobalId();

		    if (i < 15) {
			if (datapoints[i] > datapoints[i + 1])
			    averageLoss[0] = averageLoss[0] + datapoints[i] - datapoints[i + 1];
			else
			    averageGain[0] = averageGain[0] + datapoints[i + 1] - datapoints[i];
		    }
		    if (i == 15) {
			averageGain[0] /= 14f;
			averageLoss[0] /= 14f;
		    }

		    if (i > 15) {
			float lastDatapoint = datapoints[i];
			float currentDatapoint = datapoints[i + 1];

			if (lastDatapoint < currentDatapoint)
			    averageGain[0] = (averageGain[0] * 13.0f + currentDatapoint) / 14.0f;
			else
			    averageLoss[0] = (averageLoss[0] * 13.0f + currentDatapoint) / 14.0f;
		    }
		}

	    };
	}
	if (range == null)
	    range = Range.create(datapoints.length - 1);
	kernel1.execute(range);

	float relativeStrength = averageGain[0] / averageLoss[0];

	// System.out.println("Relative Strength: " + (100.0 - (100.0 / (1.0 +
	// relativeStrength))));

	return 100.0 - (100.0 / (1.0 + relativeStrength));
    }

}
