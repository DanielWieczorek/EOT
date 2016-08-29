package de.wieczorek.eot.domain;

import java.time.LocalDateTime;

public class TimedExchangeRate {

	private ExchangableType from;
	private ExchangableType to;
	private double toPrice;
	private LocalDateTime time;
	
	public TimedExchangeRate(ExchangableType from, ExchangableType to, double toPrice, LocalDateTime time) {
		super();
		this.from = from;
		this.to = to;
		this.toPrice = toPrice;
		this.time = time;
	}

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

	public double getToPrice() {
		return toPrice;
	}

	public void setToPrice(double toPrice) {
		this.toPrice = toPrice;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
	public boolean isBefore(TimedExchangeRate exchangeRate){
		return this.time.isBefore(exchangeRate.time);
	}
	
	public TimedExchangeRate swap(){
		return new TimedExchangeRate(to, from,1/toPrice, time);
	}
		
}
