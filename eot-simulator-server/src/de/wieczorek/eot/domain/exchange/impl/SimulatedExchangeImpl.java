package de.wieczorek.eot.domain.exchange.impl;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.wieczorek.eot.business.IBusinessLayerFacade;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.OrderType;
import de.wieczorek.eot.domain.exchange.order.IOrderBook;
import de.wieczorek.eot.domain.exchange.order.impl.SimulatedOrderBookImpl;
import de.wieczorek.eot.domain.trader.Trader;

/**
 * Class representing an exchange used for simulation purposes. It does not
 * access any web service and holds only simulated exchange rates. Submitted
 * orders are not executed against a real API but automatically executed
 * internally according to the internal exchange rate.
 *
 * @author Daniel Wieczorek
 *
 */
@Singleton
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
     * The tax in percent for each order.
     */
    private double orderFees = 0.2;

    private int orderExecutionTimeInMinutes = 15;

    /**
     * Constructor.
     *
     * @param historyUc
     *            the history uc used to get the history data.
     */
    @Inject
    public SimulatedExchangeImpl(final IBusinessLayerFacade businessLayer, final IOrderBook orderBook) {
	super(businessLayer, orderBook);
	orderExecutionTimeInMinutes = businessLayer.getOrderExecutionTime();
	orderFees = businessLayer.getOrderFees();
    }

    @Override
    public final TimedExchangeRate getCurrentExchangeRate(final ExchangablePair pair) {
	return currentExchangeRate;

    }

    @Override
    public final ExchangableSet performOrder(final Order o, final Trader trader) {
	final int percentageMax = 100;
	TimedExchangeRate rate = new TimedExchangeRate(currentExchangeRate.getFrom(), currentExchangeRate.getTo(),
		currentExchangeRate.getToPrice(), currentExchangeRate.getTime());

	orderBook.addOrder(o, trader, currentExchangeRate.getTime());
	if (o.getType().equals(OrderType.SELL)) {
	    return new ExchangableSet(o.getPair().getTo(),
		    o.getAmount() * rate.getToPrice() * (1 - orderFees / percentageMax));
	} else {
	    rate = rate.swap();
	    return new ExchangableSet(o.getPair().getFrom(),
		    o.getAmount() * rate.getToPrice() * (1 - orderFees / percentageMax));
	}

    }

    @Override
    public final ExchangeRateHistory getExchangeRateHistory(final ExchangablePair pair, final int minutes) {
	if (history == null) {
	    setHistory(super.getExchangeRateHistory(pair, minutes));
	    return getHistory();
	}

	return history.getHistoryEntriesBefore(currentExchangeRate.getTime(), minutes);
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

	this.history = historyInput;
	resetHistoryIterator();
    }

    /**
     * iterates one step in the internal history.
     */
    public final void icrementTime() {
	currentExchangeRate = iter.next();
	((SimulatedOrderBookImpl) orderBook).cleanup(currentExchangeRate.getTime(), orderExecutionTimeInMinutes);
    }

    @Override
    public List<Order> getCurrentOrders(final Trader trader) {
	return orderBook.getOrderByTrader(trader);
    }

    public void resetHistoryIterator() {
	final int startPoint = 15 * 60;
	if (history != null) {
	    iter = history.getCompleteHistoryData().listIterator(startPoint);
	    currentExchangeRate = iter.next();
	}

    }

    public void reset() {
	resetHistoryIterator();
	((SimulatedOrderBookImpl) orderBook).removeAllOrders();
    }

    @Override
    public LocalDateTime getTime() {

	return LocalDateTime.from(currentExchangeRate.getTime());
    }

}
