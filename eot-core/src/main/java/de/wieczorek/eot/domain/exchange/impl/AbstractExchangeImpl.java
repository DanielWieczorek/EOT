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

    protected final IOrderBook orderBook;
    protected final IBusinessLayerFacade businessLayer;

    /**
     * Constructor.
     *
     * @param historyUciInput
     *            the exchange rate history uc.
     * @param exchangeRateUcInput
     *            the exchange rate uc.
     */
    public AbstractExchangeImpl(IBusinessLayerFacade businessLayer, IOrderBook orderBookInput) {
	this.orderBook = orderBookInput;
	this.businessLayer = businessLayer;
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
