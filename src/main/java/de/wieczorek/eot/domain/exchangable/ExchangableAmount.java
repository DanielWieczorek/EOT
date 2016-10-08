package de.wieczorek.eot.domain.exchangable;

public class ExchangableAmount {

	private double aquiredAtPrice; // TODO implementation so that this is the BTC equivalent
	private ExchangableSet exchangableAmount;
	
	public ExchangableAmount(ExchangableSet exchangableAmount, double aquiredAtPrice){
		this.exchangableAmount = exchangableAmount;
		this.aquiredAtPrice = aquiredAtPrice;
	}
	
	public ExchangableAmount(){
		
	}
	
	public double getAquiredAtPrice() {
		return aquiredAtPrice;
	}
	public void setAquiredAtPrice(double aquiredAtPrice) {
		this.aquiredAtPrice = aquiredAtPrice;
	}
	public ExchangableSet getExchangableAmount() {
		return exchangableAmount;
	}
	public void setExchangableAmount(ExchangableSet exchangableAmount) {
		this.exchangableAmount = exchangableAmount;
	}
}
