package de.wieczorek.eot.domain.exchange.order;

import java.time.LocalDateTime;
import java.util.List;

import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.trader.Trader;

/**
 * The order book which contains all orders which are currently pending.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IOrderBook {

    /**
     * Retrieves all pending orders for a trader.
     * 
     * @param trader
     *            the trader to retrieve the orders for.
     * @return a list of orders
     */
    List<Order> getOrderByTrader(Trader trader);

    /**
     * retrieves all orders for all traders.
     * 
     * @return a list of orders
     */
    List<Order> getAllOrders();

    /**
     * Synchronizes the internal order book with the api.
     */
    void synchronize();

    /**
     * Adds an order to the order book.
     * 
     * @param order
     *            the order itself
     * @param trader
     *            the trader performing the order
     * @param time
     *            the timestamp of the order
     */
    void addOrder(Order order, Trader trader, LocalDateTime time);
}
