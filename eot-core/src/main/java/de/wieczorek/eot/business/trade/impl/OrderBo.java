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

    private double price;

    /**
     * 
     */
    private OrderExecutionType executionType;

    public ExchangablePair getPair() {
	return pair;
    }

    public void setPair(ExchangablePair pair) {
	this.pair = pair;
    }

    public double getVolume() {
	return volume;
    }

    public void setVolume(double volume) {
	this.volume = volume;
    }

    public OrderType getType() {
	return type;
    }

    public void setType(OrderType type) {
	this.type = type;
    }

    public OrderExecutionType getExecutionType() {
	return executionType;
    }

    public void setExecutionType(OrderExecutionType executionType) {
	this.executionType = executionType;
    }

    public double getPrice() {
	return price;
    }

    public void setPrice(double price) {
	this.price = price;
    }

}
