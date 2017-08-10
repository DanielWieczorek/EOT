package de.wieczorek.eot.business.trade.impl;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchange.OrderType;

/**
 * BO for calls against the exchange api. This BO represents the order.
 * 
 * @author Daniel Wieczorek
 *
 */
public class OrderBo {
    /**
     * the pair to be traded between.
     */
    private ExchangablePair pair;

    /**
     * the amount of the exchangable to be traded in lots.
     */
    private double volume;
    /**
     * Type of the order: Buy or sell.
     */
    private OrderType type;

    /**
     * Price at which the buy/sell should be peformed.
     */
    private double price;

    /**
     * 
     */
    private OrderExecutionType executionType;

    public final ExchangablePair getPair() {
	return pair;
    }

    public final void setPair(final ExchangablePair pair) {
	this.pair = pair;
    }

    public final double getVolume() {
	return volume;
    }

    public final void setVolume(final double volume) {
	this.volume = volume;
    }

    public final OrderType getType() {
	return type;
    }

    public final void setType(final OrderType type) {
	this.type = type;
    }

    public final OrderExecutionType getExecutionType() {
	return executionType;
    }

    public final void setExecutionType(final OrderExecutionType executionType) {
	this.executionType = executionType;
    }

    public final double getPrice() {
	return price;
    }

    public final void setPrice(final double price) {
	this.price = price;
    }

}
