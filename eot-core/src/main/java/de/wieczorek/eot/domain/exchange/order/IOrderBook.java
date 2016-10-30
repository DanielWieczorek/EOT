package de.wieczorek.eot.domain.exchange.order;

import java.time.LocalDateTime;
import java.util.List;

import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.trader.Trader;

public interface IOrderBook {

    List<Order> getOrderByTrader(Trader trader);

    List<Order> getAllOrders();

    void synchronize();

    void addOrder(Order order, Trader trader, LocalDateTime time);
}
