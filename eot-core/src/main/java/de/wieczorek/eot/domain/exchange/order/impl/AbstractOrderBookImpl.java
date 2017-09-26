package de.wieczorek.eot.domain.exchange.order.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.order.IOrderBook;
import de.wieczorek.eot.domain.trader.Trader;

/**
 * Abstract implementation of {@link IOrderBook}.
 * 
 * @author Daniel Wieczorek
 *
 */
public abstract class AbstractOrderBookImpl implements IOrderBook {

    /**
     * Map containing all orders. The orders are assigned to the trader.
     * Additionally the order is wrapped into an {@link OrderInfo} object which
     * contains the time of the order execution.
     */
    protected Map<Trader, List<OrderInfo>> orders;

    /**
     * Constructor.
     */
    public AbstractOrderBookImpl() {
	this.orders = new ConcurrentHashMap<>();
    }

    @Override
    public final List<Order> getOrderByTrader(final Trader trader) {
	List<Order> result = new ArrayList<>();
	List<OrderInfo> orderInfos = orders.get(trader);
	if (orderInfos == null) {
	    return new ArrayList<Order>();
	} else {
	    orderInfos = new ArrayList<>(orderInfos);
	    for (OrderInfo info : orderInfos) {
		if (info != null) {
		    result.add(info.getOrder());
		}
	    }
	}
	return result;
    }

    @Override
    public final List<Order> getAllOrders() {

	return null;
    }

    /**
     * Class containing the order information with the submission time stamp.
     * 
     * @author Daniel Wieczorek
     *
     */
    protected class OrderInfo {

	/**
	 * The order.
	 */
	private Order order;

	/**
	 * Timestamp of submission.
	 */
	private LocalDateTime timeAdded;

	/**
	 * Constructor.
	 * 
	 * @param orderInput
	 *            the order
	 * @param addedInput
	 *            the submisson timestamp
	 */
	public OrderInfo(final Order orderInput, final LocalDateTime addedInput) {
	    this.setOrder(orderInput);
	    this.setTimeAdded(addedInput);

	}

	public final Order getOrder() {
	    return order;
	}

	public final void setOrder(final Order orderInput) {
	    this.order = orderInput;
	}

	public final LocalDateTime getTimeAdded() {
	    return timeAdded;
	}

	public final void setTimeAdded(final LocalDateTime timeAddedInput) {
	    this.timeAdded = timeAddedInput;
	}
    }

    @Override
    public final void addOrder(final Order order, final Trader trader, final LocalDateTime time) {
	List<OrderInfo> existingOrders = orders.get(trader);
	if (existingOrders == null) {
	    existingOrders = new ArrayList<>();
	    existingOrders.add(new OrderInfo(order, time));
	    orders.put(trader, existingOrders);
	} else {
	    existingOrders.add(new OrderInfo(order, time));
	}

    }

    @Override
    public abstract void synchronize();

}
