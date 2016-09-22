package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.impl.ExchangablePair;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.Order;

public interface IExchange {
	public ExchangeRateHistory getExchangeRateHistory(ExchangablePair pair, int hours);

	public TimedExchangeRate getCurrentExchangeRate(ExchangablePair pair);

	public ExchangableSet performOrder(Order o);
}
