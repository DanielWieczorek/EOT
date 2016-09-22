package de.wieczorek.eot.domain.exchange.impl;

import de.wieczorek.eot.domain.ExchangableType;

public class ExchangablePair {
	private ExchangableType from;
	private ExchangableType to;
	public ExchangableType getFrom() {
		return from;
	}
	public void setFrom(ExchangableType from) {
		this.from = from;
	}
	public ExchangableType getTo() {
		return to;
	}
	public void setTo(ExchangableType to) {
		this.to = to;
	}
	
	public ExchangablePair(ExchangableType from, ExchangableType to) {
		super();
		this.from = from;
		this.to = to;
	}
}
