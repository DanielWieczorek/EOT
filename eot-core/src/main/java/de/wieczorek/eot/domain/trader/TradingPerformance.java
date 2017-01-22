package de.wieczorek.eot.domain.trader;

import java.util.Observable;
import java.util.Observer;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;

/**
 * Class which measures the performance of a trader.
 * 
 * @author Daniel Wieczorek
 *
 */
public class TradingPerformance implements Observer {

    /**
     * The number of trades done since the start of the collection of the data.
     */
    private int numberOfTrades = 0;

    /**
     * The full profit in BTC since the start of the collection of the data.
     */
    private double netProfit = 0.0;

    /**
     * The full profit in percent since the start of the collection of the data.
     */
    private double netProfitPercent = 0.0;

    /**
     * Number of trades with negative returns since the start of the collection
     * of the data.
     */
    private int numberOfTradesWithLosses = 0;

    /**
     * The initial amount of exchangables.
     */
    private ExchangableSet startValue;

    /**
     * Constructor.
     * 
     * @param startValueInput
     *            the starting amount of exchangables of the trader being
     *            observed.
     */
    public TradingPerformance(final ExchangableSet startValueInput) {
	this.startValue = startValueInput;
    }

    @Override
    public final void update(final Observable arg0, final Object arg1) {
	Trader trader = (Trader) arg0;
	final int maxPercent = 100;

	setNumberOfTrades(getNumberOfTrades() + 1);
	ExchangableSet from = trader.getAccount().countAllExchangablesOfType(trader.getExchangablesToTrade().getFrom());
	ExchangableSet to = trader.getAccount().countAllExchangablesOfType(trader.getExchangablesToTrade().getTo());

	ExchangableSet result;
	if (startValue.getExchangable() == to.getExchangable()) {
	    result = new ExchangableSet(to.getExchangable(), to.getAmount() + from.getAmount()
		    * trader.getExchange().getCurrentExchangeRate(trader.getExchangablesToTrade()).getToPrice());
	} else {
	    result = new ExchangableSet(to.getExchangable(), to.getAmount() + from.getAmount() * 1.0
		    / trader.getExchange().getCurrentExchangeRate(trader.getExchangablesToTrade()).getToPrice());
	}

	double netProfitOld = getNetProfit();
	setNetProfit(result.getAmount() - startValue.getAmount());
	setNetProfitPercent(getNetProfit() * maxPercent / startValue.getAmount());
	if (netProfitOld > getNetProfit()) {
	    setNumberOfTradesWithLosses(getNumberOfTradesWithLosses() + 1);
	}

    }

    public final int getNumberOfTrades() {
	return numberOfTrades;
    }

    public final void setNumberOfTrades(final int numberOfTradesInput) {
	this.numberOfTrades = numberOfTradesInput;
    }

    public final double getNetProfitPercent() {
	return netProfitPercent;
    }

    public final void setNetProfitPercent(final double netProfitPercentInput) {
	this.netProfitPercent = netProfitPercentInput;
    }

    public final int getNumberOfTradesWithLosses() {
	return numberOfTradesWithLosses;
    }

    public final void setNumberOfTradesWithLosses(final int numberOfTradesWithLossesInput) {
	this.numberOfTradesWithLosses = numberOfTradesWithLossesInput;
    }

    public final double getNetProfit() {
	return netProfit;
    }

    public final void setNetProfit(final double netProfitInput) {
	this.netProfit = netProfitInput;
    }

}
