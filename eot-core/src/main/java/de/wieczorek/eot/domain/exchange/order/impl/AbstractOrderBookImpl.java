package de.wieczorek.eot.domain.exchange.order.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.order.IOrderBook;
import de.wieczorek.eot.domain.trader.Trader;

public abstract class AbstractOrderBookImpl implements IOrderBook {

    protected Map<Trader, List<OrderInfo>> orders;

    public AbstractOrderBookImpl() {
	this.orders = new HashMap<>();
    }

    @Override
    public List<Order> getOrderByTrader(Trader trader) {
	List<Order> result = new LinkedList<>();
	List<OrderInfo> orderInfos = orders.get(trader);
	if (orderInfos == null)
	    return new LinkedList<Order>();
	else
	    for (OrderInfo info : orderInfos)
		result.add(info.getOrder());
	return result;
    }

    @Override
    public List<Order> getAllOrders() {

	return null;
    }

    protected class OrderInfo {

	private Order order;
	private LocalDateTime timeAdded;

	public OrderInfo(Order order, LocalDateTime added) {
	    this.setOrder(order);
	    this.setTimeAdded(added);

	}

	public Order getOrder() {
	    return order;
	}

	public void setOrder(Order order) {
	    this.order = order;
	}

	public LocalDateTime getTimeAdded() {
	    return timeAdded;
	}

	public void setTimeAdded(LocalDateTime timeAdded) {
	    this.timeAdded = timeAdded;
	}
    }

    @Override
    public void addOrder(Order order, Trader trader, LocalDateTime time) {
	List<OrderInfo> existingOrders = orders.get(order);
	if (existingOrders == null) {
	    existingOrders = new LinkedList<>();
	    existingOrders.add(new OrderInfo(order, time));
	    orders.put(trader, existingOrders);
	} else
	    existingOrders.add(new OrderInfo(order, time));

    }

    @Override
    public abstract void synchronize();

}
