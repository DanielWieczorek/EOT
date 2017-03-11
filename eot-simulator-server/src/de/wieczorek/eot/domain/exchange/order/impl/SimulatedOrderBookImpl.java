package de.wieczorek.eot.domain.exchange.order.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SimulatedOrderBookImpl extends AbstractOrderBookImpl {

    public SimulatedOrderBookImpl() {
	super();
    }

    @Override
    public void synchronize() {

    }

    public void cleanup(final LocalDateTime currentTime, final int minutes) {
	for (final List<OrderInfo> orderInfoList : orders.values()) {
	    final List<OrderInfo> temp = orderInfoList.stream()
		    .filter(info -> info.getTimeAdded().plusMinutes(minutes).isAfter(currentTime))
		    .collect(Collectors.toList());
	    orderInfoList.clear();
	    orderInfoList.addAll(temp);

	}

    }

    public void removeAllOrders() {
	orders.clear();
    }
}
