package de.wieczorek.eot.domain.trader;

import java.time.LocalDateTime;

import de.wieczorek.eot.domain.exchange.Order;

public class TraderMemory {

    private Order lastOrder;
    private LocalDateTime lastOrderDate;

    public TraderMemory() {
    }

    public Order getLastOrder() {
	return lastOrder;
    }

    public void setLastOrder(Order lastOrder) {
	this.lastOrder = lastOrder;
    }

    public LocalDateTime getLastOrderDate() {
	return lastOrderDate;
    }

    public void setLastOrderDate(LocalDateTime lastOrderDate) {
	this.lastOrderDate = lastOrderDate;
    }

}
