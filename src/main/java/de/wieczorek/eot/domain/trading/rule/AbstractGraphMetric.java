package de.wieczorek.eot.domain.trading.rule;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public abstract class AbstractGraphMetric {
	protected ExchangeRateHistory data;
	public abstract double getRating(ExchangeRateHistory history);
}
