package de.wieczorek.eot.domain.exchange;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.trader.Trader;

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
     * @param t
     *            the trader for which the order is executed
     * @return The return of the order execution.
     */
    ExchangableSet performOrder(Order o, Trader t);

    /**
     * Returns the currently pending orders for the given trader.
     * 
     * @param trader
     *            the trader for which the orders are searched
     * @return a list of orders. The list is empty if there was no order
     */
    List<Order> getCurrentOrders(Trader trader);
}
