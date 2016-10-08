package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.impl.ExchangablePair;
import de.wieczorek.eot.domain.trader.ExchangableSet;
import de.wieczorek.eot.domain.trader.Order;

/**
 * Interface for Exchanges. Exchanges perform orders of traders or provide
 * information regarding the current marked situation.
 *
 * @author Daniel Wieczorek
 *
 */
public interface IExchange {
    /**
     * Returns the history of exchange rates in regard to a specified market.
     *
     * @param pair
     *            a pair of exchangeable goods which can be traded.
     * @param hours
     *            the length of the history
     * @return the exchange rate history.
     */
    ExchangeRateHistory getExchangeRateHistory(ExchangablePair pair, int hours);

    /**
     * Returns the exchange rate in regard to a specified market.
     *
     * @param pair
     *            a pair of exchangeable goods which can be traded.
     * @return the current exchange
     */
    TimedExchangeRate getCurrentExchangeRate(ExchangablePair pair);

    /**
     * Executes the provided order.
     *
     * @param o
     *            the order
     * @return The return of the order execution.
     */
    ExchangableSet performOrder(Order o);
}
