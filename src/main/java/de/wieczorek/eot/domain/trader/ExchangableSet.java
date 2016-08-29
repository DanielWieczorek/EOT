package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangableType;

public class ExchangableSet {

	private ExchangableType exchangable;
	private double amount;
	
	public ExchangableSet mergeWith(ExchangableSet mergePartner){
		// TODO exception if types are different
		return new ExchangableSet(exchangable, amount + mergePartner.amount);
	}
	
	public ExchangableSet(){
		
	}
	
	public ExchangableSet(ExchangableType exchangable, double amount){
		this.exchangable = exchangable;
		this.amount = amount;
	}
	
	public ExchangableType getExchangable() {
		return exchangable;
	}
	public void setExchangable(ExchangableType exchangable) {
		this.exchangable = exchangable;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
