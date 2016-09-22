package de.wieczorek.eot.domain.exchange.impl;

import java.util.Iterator;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.Order;
import de.wieczorek.eot.domain.trader.OrderType;

public class SimulatedExchangeImpl extends AbstractExchangeImpl {

	private ExchangeRateHistory history = null;
	private Iterator<TimedExchangeRate> iter;
	private TimedExchangeRate currentExchangeRate;

	public SimulatedExchangeImpl(IChartHistoryUc historyUc, IExchangeRateUc exchangeRateUc) {
		super(historyUc, exchangeRateUc);

	}

	@Override
	public TimedExchangeRate getCurrentExchangeRate(ExchangablePair pair) {
		return currentExchangeRate;

	}

	@Override
	public ExchangableSet performOrder(Order o) {
		TimedExchangeRate rate = new TimedExchangeRate(currentExchangeRate.getFrom(), currentExchangeRate.getTo(),
				currentExchangeRate.getToPrice(), currentExchangeRate.getTime());
		if (o.getType().equals(OrderType.BUY)) {
			return new ExchangableSet(o.getPair().getTo(), o.getAmount() * rate.getToPrice());
		} else {
			rate = rate.swap();
			return new ExchangableSet(o.getPair().getFrom(), o.getAmount() * rate.getToPrice());
		}
	}

	@Override
	public ExchangeRateHistory getExchangeRateHistory(ExchangablePair pair, int hours) {
		if (history == null)
			setHistory(super.getExchangeRateHistory(pair, hours));

		return history.getHistoryEntriesBefore(currentExchangeRate.getTime(), hours * 60);
	}

	public ExchangeRateHistory getHistory() {
		return history;
	}

	public void setHistory(ExchangeRateHistory history) {
		this.history = history;
		if (history != null) {
			iter = history.getCompleteHistoryData().listIterator(15 * 30);
			currentExchangeRate = iter.next();
		}
	}

	public void icrementTime() {
		currentExchangeRate = iter.next();
		// System.out.println("CurrentTime: " + currentExchangeRate.getTime());
	}

}
