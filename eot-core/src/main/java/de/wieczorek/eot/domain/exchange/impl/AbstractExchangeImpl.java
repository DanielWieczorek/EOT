package de.wieczorek.eot.domain.exchange.impl;

import de.wieczorek.eot.business.IBusinessLayerFacade;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.order.IOrderBook;
import de.wieczorek.eot.domain.trader.Trader;

/**
 * Abstract superclass for all exchanges.
 *
 * @author Daniel Wieczorek
 *
 */
public abstract class AbstractExchangeImpl implements IExchange {

    /**
     * The order book of this exchange.
     */
    protected final IOrderBook orderBook;

    /**
     * Business layer facade needed to access the API of the exchange to e.g.
     * execute the orders.
     */
    protected final IBusinessLayerFacade businessLayer;

    /**
     * Constructor.
     *
     * @param businessLayerInput
     *            the interface to the business layer
     * @param orderBookInput
     *            the order book
     */
    public AbstractExchangeImpl(final IBusinessLayerFacade businessLayerInput, final IOrderBook orderBookInput) {
	this.orderBook = orderBookInput;
	this.businessLayer = businessLayerInput;
    }

    @Override
    public ExchangeRateHistory getExchangeRateHistory(final ExchangablePair pair, final int hours) {

	return businessLayer.getDetailedHistoryFromDb(pair.getFrom(), pair.getTo(), hours);
    }

    @Override
    public TimedExchangeRate getCurrentExchangeRate(final ExchangablePair pair) {

	return businessLayer.getCurrentExchangeRate(pair.getFrom(), pair.getTo());
    }

    @Override
    public abstract ExchangableSet performOrder(Order o, Trader trader);

}
