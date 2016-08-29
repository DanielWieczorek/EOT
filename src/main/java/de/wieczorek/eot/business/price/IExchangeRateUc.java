package de.wieczorek.eot.business.price;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.TimedExchangeRate;

public interface IExchangeRateUc {

	public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to);
}
