package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangeRateHistory;

public abstract class AbstractGraphMetric {
	protected ExchangeRateHistory data;
	public abstract double getRating(ExchangeRateHistory history);
}
