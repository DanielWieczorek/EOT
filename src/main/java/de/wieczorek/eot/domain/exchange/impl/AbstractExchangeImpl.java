package de.wieczorek.eot.domain.exchange.impl;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.Order;

public abstract class AbstractExchangeImpl implements IExchange {

	protected IChartHistoryUc historyUc;
	protected IExchangeRateUc exchangeRateUc;

	public AbstractExchangeImpl(IChartHistoryUc historyUc, IExchangeRateUc exchangeRateUc) {
		this.historyUc = historyUc;
		this.exchangeRateUc = exchangeRateUc;
	}

	@Override
	public ExchangeRateHistory getExchangeRateHistory(ExchangablePair pair, int hours) {

		return historyUc.getDetailedHistory(pair.getFrom(), pair.getTo(), hours);
	}

	@Override
	public TimedExchangeRate getCurrentExchangeRate(ExchangablePair pair) {

		return exchangeRateUc.getCurrentExchangeRate(pair.getFrom(), pair.getTo());
	}

	@Override
	public abstract ExchangableSet performOrder(Order o);
}
