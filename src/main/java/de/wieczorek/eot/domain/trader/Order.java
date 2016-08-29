package de.wieczorek.eot.domain.trader;

public class Order {

	private ExchangableSet from;
	private ExchangableSet to;
	private OrderType type;
	public ExchangableSet getFrom() {
		return from;
	}
	public void setFrom(ExchangableSet from) {
		this.from = from;
	}
	public ExchangableSet getTo() {
		return to;
	}
	public void setTo(ExchangableSet to) {
		this.to = to;
	}
	public OrderType getType() {
		return type;
	}
	public void setType(OrderType type) {
		this.type = type;
	}

}
