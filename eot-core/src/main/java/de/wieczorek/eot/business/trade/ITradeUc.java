package de.wieczorek.eot.business.trade;

import de.wieczorek.eot.domain.exchange.Order;

/**
 * Contains all the logic to execute orders.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface ITradeUc {
    /**
     * Executes the given order.
     * 
     * @param order
     *            the order to execute
     */
    void perform(Order order);

}
