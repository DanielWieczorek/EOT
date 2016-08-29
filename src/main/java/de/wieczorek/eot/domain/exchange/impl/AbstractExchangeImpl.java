package de.wieczorek.eot.domain.exchange.impl;

import java.util.List;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.Order;
import de.wieczorek.eot.domain.trader.OrderType;

public abstract class AbstractExchangeImpl implements IExchange{
	
	protected IChartHistoryUc historyUc;
	protected IExchangeRateUc exchangeRateUc;

	public AbstractExchangeImpl(IChartHistoryUc historyUc, IExchangeRateUc exchangeRateUc){
		this.historyUc = historyUc;
		this.exchangeRateUc = exchangeRateUc;
	}

	@Override
	public ExchangeRateHistory getExchangeRateHistory(ExchangableType from, ExchangableType to, int hours) {
		
		return historyUc.getHistory(from, to, hours);
	}

	@Override
	public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to) {
		
		return exchangeRateUc.getCurrentExchangeRate(from, to);
	}

	@Override
	public abstract void performOrder(Order o);
}
