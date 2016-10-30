package de.wieczorek.eot.domain.exchange.impl;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
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
     * The uc that provides the exchange rate history data.
     */
    private final IChartHistoryUc historyUc;

    /**
     * the uc providing the current exchange rate.
     */
    private final IExchangeRateUc exchangeRateUc;

    protected final IOrderBook orderBook;

    /**
     * Constructor.
     *
     * @param historyUciInput
     *            the exchange rate history uc.
     * @param exchangeRateUcInput
     *            the exchange rate uc.
     */
    public AbstractExchangeImpl(final IChartHistoryUc historyUciInput, final IExchangeRateUc exchangeRateUcInput,
	    IOrderBook orderBookInput) {
	this.historyUc = historyUciInput;
	this.exchangeRateUc = exchangeRateUcInput;
	this.orderBook = orderBookInput;
    }

    @Override
    public ExchangeRateHistory getExchangeRateHistory(final ExchangablePair pair, final int hours) {

	return historyUc.getDetailedHistoryFromDb(pair.getFrom(), pair.getTo(), hours);
    }

    @Override
    public TimedExchangeRate getCurrentExchangeRate(final ExchangablePair pair) {

	return exchangeRateUc.getCurrentExchangeRate(pair.getFrom(), pair.getTo());
    }

    @Override
    public abstract ExchangableSet performOrder(Order o, Trader trader);

}
