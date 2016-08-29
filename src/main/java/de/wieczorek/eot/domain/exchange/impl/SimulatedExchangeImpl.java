package de.wieczorek.eot.domain.exchange.impl;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.trader.Order;
import de.wieczorek.eot.domain.trader.OrderType;

public class SimulatedExchangeImpl extends AbstractExchangeImpl {
	
	private ExchangeRateHistory history = null;
	private int indexUsedLast = 15;

	public SimulatedExchangeImpl(IChartHistoryUc historyUc, IExchangeRateUc exchangeRateUc) {
		super(historyUc, exchangeRateUc);
		
	}

	@Override
	public void performOrder(Order o) {
		TimedExchangeRate rate = new TimedExchangeRate(history.getCompleteHistoryData().get(getIndexUsedLast()).getFrom(), history.getCompleteHistoryData().get(getIndexUsedLast()).getTo(), history.getCompleteHistoryData().get(getIndexUsedLast()).getToPrice(), history.getCompleteHistoryData().get(getIndexUsedLast()).getTime());
		if (o.getType().equals(OrderType.BUY)){
			o.getTo().setAmount(o.getFrom().getAmount()*rate.getToPrice());
		}
		else {
			rate = rate.swap();
			o.getFrom().setAmount(o.getTo().getAmount()*rate.getToPrice()*0.998);
		}

	}

	@Override
	public ExchangeRateHistory getExchangeRateHistory(ExchangableType from, ExchangableType to, int hours) {
		if(history == null)
			history = super.getExchangeRateHistory(from, to, hours);
	
		return history.getHistoryEntriesBefore(history.getCompleteHistoryData().get(getIndexUsedLast()).getTime(), 1000);
	}

	public ExchangeRateHistory getHistory() {
		return history;
	}

	public void setHistory(ExchangeRateHistory history) {
		this.history = history;
	}

	public int getIndexUsedLast() {
		return indexUsedLast;
	}

	public void setIndexUsedLast(int indexUsedLast) {
		this.indexUsedLast = indexUsedLast;
	}

}
