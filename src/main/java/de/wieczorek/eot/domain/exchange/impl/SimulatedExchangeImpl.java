package de.wieczorek.eot.domain.exchange.impl;

import java.util.Iterator;

import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.Order;
import de.wieczorek.eot.domain.trader.OrderType;

/**
 * Class representing an exchange used for simulation purposes. It does not
 * access any web service and holds only simulated exchange rates. Submitted
 * orders are not executed against a real API but automatically executed
 * internally according to the internal exchange rate.
 *
 * @author Daniel Wieczorek
 *
 */
public class SimulatedExchangeImpl extends AbstractExchangeImpl {

    /**
     * The internal exchange rate history.
     */
    private ExchangeRateHistory history = null;
    /**
     * An iterator to go over the internal history sequentially.
     */
    private Iterator<TimedExchangeRate> iter;
    /**
     * represents the current internal exchange rate.
     */
    private TimedExchangeRate currentExchangeRate;

    /**
     *
     * @param historyUc
     * @param exchangeRateUc
     */
    public SimulatedExchangeImpl() {
	super(null, null);

    }

    @Override
    public final TimedExchangeRate getCurrentExchangeRate(final ExchangablePair pair) {
	return currentExchangeRate;

    }

    @Override
    public final ExchangableSet performOrder(final Order o) {
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
    public final ExchangeRateHistory getExchangeRateHistory(final ExchangablePair pair, final int hours) {
	final int minutesPerHour = 60;
	if (history == null) {
	    setHistory(super.getExchangeRateHistory(pair, hours));
	}

	return history.getHistoryEntriesBefore(currentExchangeRate.getTime(), hours * minutesPerHour);
    }

    public final ExchangeRateHistory getHistory() {
	return history;
    }

    /**
     * Sets the history over which is iterated.
     *
     * @param historyInput
     *            the exchange rate history to set
     */
    public final void setHistory(final ExchangeRateHistory historyInput) {
	final int startPoint = 15 * 60;
	this.history = historyInput;
	if (historyInput != null) {
	    iter = historyInput.getCompleteHistoryData().listIterator(startPoint);
	    currentExchangeRate = iter.next();
	}
    }

    /**
     * iterates one step in the internal history.
     */
    public final void icrementTime() {
	currentExchangeRate = iter.next();
	// System.out.println("CurrentTime: " + currentExchangeRate.getTime());
    }

}
