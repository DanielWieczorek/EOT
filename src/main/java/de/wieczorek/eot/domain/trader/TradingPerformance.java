package de.wieczorek.eot.domain.trader;

import java.util.Observable;
import java.util.Observer;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchange.Order;

public class TradingPerformance implements Observer {

	private int numberOfTrades = 0;
	private double netProfit = 0.0;
	private double netProfitPercent = 0.0;
	private int numberOfTradesWithLosses = 0;
	private ExchangableSet startValue;

	public TradingPerformance(ExchangableSet startValue) {
		this.startValue = startValue;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Trader trader = (Trader) arg0;
		Order order = (Order) arg1;
		setNumberOfTrades(getNumberOfTrades() + 1);
		ExchangableSet from = trader.getWallet().countAllExchangablesOfType(trader.getExchangablesToTrade().getFrom());
		ExchangableSet to = trader.getWallet().countAllExchangablesOfType(trader.getExchangablesToTrade().getTo());

		ExchangableSet result;
		if (startValue.getExchangable() == to.getExchangable())
			result = new ExchangableSet(to.getExchangable(), to.getAmount() + from.getAmount()
					* trader.getExchange().getCurrentExchangeRate(trader.getExchangablesToTrade()).getToPrice());
		else
			result = new ExchangableSet(to.getExchangable(), to.getAmount() + from.getAmount() * 1.0
					/ trader.getExchange().getCurrentExchangeRate(trader.getExchangablesToTrade()).getToPrice());

		double netProfitOld = getNetProfit();
		setNetProfit(result.getAmount() - startValue.getAmount());
		setNetProfitPercent(getNetProfit() * 100 / startValue.getAmount());
		if (netProfitOld > getNetProfit())
			setNumberOfTradesWithLosses(getNumberOfTradesWithLosses() + 1);

	}

	public int getNumberOfTrades() {
		return numberOfTrades;
	}

	public void setNumberOfTrades(int numberOfTrades) {
		this.numberOfTrades = numberOfTrades;
	}

	public double getNetProfitPercent() {
		return netProfitPercent;
	}

	public void setNetProfitPercent(double netProfitPercent) {
		this.netProfitPercent = netProfitPercent;
	}

	public int getNumberOfTradesWithLosses() {
		return numberOfTradesWithLosses;
	}

	public void setNumberOfTradesWithLosses(int numberOfTradesWithLosses) {
		this.numberOfTradesWithLosses = numberOfTradesWithLosses;
	}

	public double getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(double netProfit) {
		this.netProfit = netProfit;
	}

}
