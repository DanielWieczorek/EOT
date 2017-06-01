package de.wieczorek.eot.domain.exchange.impl;

import java.util.ArrayList;
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
public class RealExchangeImpl extends AbstractExchangeImpl {

    /**
     * represents the current internal exchange rate.
     */
    private TimedExchangeRate currentExchangeRate;

    /**
     * Constructor.
     *
     * @param historyUc
     *            the history uc used to get the history data.
     */
    @Inject
    public RealExchangeImpl(final IBusinessLayerFacade businessLayer, final IOrderBook orderBook) {
	super(businessLayer, orderBook);
    }

    @Override
    public final TimedExchangeRate getCurrentExchangeRate(final ExchangablePair pair) {
	return businessLayer.getCurrentExchangeRate(pair.getFrom(), pair.getTo());

    }

    @Override
    public final ExchangableSet performOrder(final Order o, final Trader trader) {
	currentExchangeRate = businessLayer.getCurrentExchangeRate(o.getPair().getFrom(), o.getPair().getTo());
	TimedExchangeRate rate = new TimedExchangeRate(o.getPair().getFrom(), o.getPair().getTo(),
		currentExchangeRate.getToPrice(), currentExchangeRate.getTime());

	currentExchangeRate = businessLayer.getCurrentExchangeRate(o.getPair().getFrom(), o.getPair().getTo());
	orderBook.addOrder(o, trader, currentExchangeRate.getTime());

	if (o.getType().equals(OrderType.SELL)) {
	    businessLayer.perform(o);
	    return new ExchangableSet(o.getPair().getTo(), o.getAmount() * rate.getToPrice());
	} else {
	    rate = rate.swap();
	    o.setAmount(o.getAmount() * rate.getToPrice());
	    businessLayer.perform(o);
	    return new ExchangableSet(o.getPair().getFrom(), o.getAmount() * rate.getToPrice());
	}

    }

    @Override
    public final ExchangeRateHistory getExchangeRateHistory(final ExchangablePair pair, final int hours) {
	return businessLayer.getDetailedHistoryFromDb(pair.getFrom(), pair.getTo(), hours);
    }

    @Override
    public List<Order> getCurrentOrders(final Trader trader) {
	return new ArrayList<Order>();// orderBook.getOrderByTrader(trader);
    }
}
