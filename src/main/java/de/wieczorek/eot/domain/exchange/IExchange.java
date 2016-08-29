package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.trader.Order;

public interface IExchange {
	public ExchangeRateHistory getExchangeRateHistory(ExchangableType from, ExchangableType to, int hours);
	public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to);
	public void performOrder(Order o);
}
