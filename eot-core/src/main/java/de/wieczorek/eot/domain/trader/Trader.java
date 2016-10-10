package de.wieczorek.eot.domain.trader;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.wieczorek.eot.domain.evolution.IIndiviual;
import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.OrderType;
import de.wieczorek.eot.domain.exchange.impl.AbstractExchangeImpl;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;

public class Trader extends Observable implements IIndiviual {

    private static final Logger logger = Logger.getLogger(Trader.class.getName());

    private Wallet wallet;
    private String name;
    private IExchange exchange;
    private TradingRulePerceptron buyRule;
    private TradingRulePerceptron sellRule;
    private ExchangablePair exchangablesToTrade;
    private double lastSeenRate = 0.0;
    private TradingPerformance performance;

    public Trader(String name, Wallet wallet, IExchange exchange, TradingRulePerceptron buyRule,
	    TradingRulePerceptron sellRule, ExchangablePair exchangablesToTrade, TradingPerformance performance) {
	super();
	this.setName(name);
	this.setWallet(wallet);
	this.setExchange(exchange);
	this.buyRule = buyRule;
	this.sellRule = sellRule;
	this.setExchangablesToTrade(exchangablesToTrade);
	this.setPerformance(performance);
    }

    @Override
    public double calculateFitness() {

	return 0;
    }

    @Override
    public void performAction() {
	trade();

    }

    private void trade() {
	if (getExchange().getCurrentExchangeRate(getExchangablesToTrade()).getToPrice() != lastSeenRate) {
	    ExchangableSet from = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	    ExchangableSet to = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	    if (buyRule.isActivated(getExchange().getExchangeRateHistory(getExchangablesToTrade(), 24))
		    && from.getAmount() > 0) {
		buy();
	    } else if (sellRule.isActivated(getExchange().getExchangeRateHistory(getExchangablesToTrade(), 24))
		    && to.getAmount() > 0) {
		sell();
	    }
	}
	lastSeenRate = getExchange().getCurrentExchangeRate(getExchangablesToTrade()).getToPrice();

    }

    private void buy() {
	ExchangableSet from = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	if (from.getAmount() > 0) {
	    Order order = new Order(getExchangablesToTrade(), from.getAmount(), OrderType.BUY);
	    ExchangableSet returnOfInvestment = getExchange().performOrder(order);

	    getWallet().withdraw(new ExchangableSet(order.getPair().getFrom(), order.getAmount()));
	    getWallet().deposit(
		    new ExchangableAmount(returnOfInvestment, order.getAmount() / returnOfInvestment.getAmount()));

	    printWalletInfo();
	    getPerformance().update(this, order);
	}
    }

    private void sell() {
	ExchangableSet to = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	if (to.getAmount() > 0) {
	    Order order = new Order(getExchangablesToTrade(), to.getAmount(), OrderType.SELL);
	    ExchangableSet returnOfInvestment = getExchange().performOrder(order);

	    getWallet().withdraw(new ExchangableSet(order.getPair().getTo(), order.getAmount()));
	    getWallet().deposit(
		    new ExchangableAmount(returnOfInvestment, order.getAmount() / returnOfInvestment.getAmount()));

	    printWalletInfo();
	    getPerformance().update(this, order);
	}

    }

    private void printWalletInfo() {
	ExchangableSet from = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	ExchangableSet to = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	logger.log(Level.INFO, "ETH -> BTC: "
		+ ((AbstractExchangeImpl) getExchange()).getCurrentExchangeRate(getExchangablesToTrade()).getToPrice());
	from = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	to = getWallet().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	logger.log(Level.INFO, "ETH: " + from.getAmount());
	logger.log(Level.INFO, "BTC: " + to.getAmount());
    }

    public Wallet getWallet() {
	return wallet;
    }

    public void setWallet(Wallet wallet) {
	this.wallet = wallet;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public ExchangablePair getExchangablesToTrade() {
	return exchangablesToTrade;
    }

    public void setExchangablesToTrade(ExchangablePair exchangablesToTrade) {
	this.exchangablesToTrade = exchangablesToTrade;
    }

    public IExchange getExchange() {
	return exchange;
    }

    public void setExchange(IExchange exchange) {
	this.exchange = exchange;
    }

    public TradingPerformance getPerformance() {
	return performance;
    }

    public void setPerformance(TradingPerformance performance) {
	this.performance = performance;
    }

}
