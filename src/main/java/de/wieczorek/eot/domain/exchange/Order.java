package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;

public class Order {

	private ExchangablePair pair;
	private double amount;
	private OrderType type;
	
	public OrderType getType() {
		return type;
	}
	public void setType(OrderType type) {
		this.type = type;
	}
	public Order(ExchangablePair pair, double amount, OrderType type) {
		super();
		this.setPair(pair);
		this.setAmount(amount);
		this.type = type;
	}
	public Order() {
		
	}
	public ExchangablePair getPair() {
		return pair;
	}
	public void setPair(ExchangablePair pair) {
		this.pair = pair;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

}
