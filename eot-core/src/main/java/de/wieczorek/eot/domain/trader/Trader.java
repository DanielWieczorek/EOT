package de.wieczorek.eot.domain.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.OrderType;
import de.wieczorek.eot.domain.exchange.impl.AbstractExchangeImpl;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron.Input;

/**
 * Class representing a trader who can trade a certain exchangable pair at a
 * certain exchange.
 * 
 * @author Daniel Wieczorek
 *
 */
public class Trader extends Observable implements IIndividual {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Trader.class.getName());

    /**
     * account containing all the exchangables the trader possesses.
     */
    private Account account;

    /**
     * Name of the trader. Used to display in the UI.
     */
    private String name;

    /***
     * The exchange the trader is trading at.
     */
    private IExchange exchange;

    /**
     * Perceptron determining whether the trader should buy an exchangable.
     */
    private TradingRulePerceptron buyRule;

    /**
     * Perceptron determining whether the trader should sell an exchangable.
     */
    private TradingRulePerceptron sellRule;

    /**
     * The exchangable pair that the trader is trading.
     */
    private ExchangablePair exchangablesToTrade;

    /**
     * Last seen exchange rate of the currency pair.
     */
    private double lastSeenRate = 0.0;

    /**
     * Measures the trading performance for the evolution algorithm.
     */
    private TradingPerformance performance;

    /**
     * Constructor.
     * 
     * @param nameInput
     *            name of the trader
     * @param accountInput
     *            the account with the exchangables
     * @param exchangeInput
     *            the exchange at which the trader should trade
     * @param buyRuleInput
     *            rule with which the trader performs buy orders
     * 
     * @param sellRuleInput
     *            rule with which the trader performs sell orders
     * @param exchangablesToTradeInput
     *            exchangable pair to trade
     * @param performanceInput
     *            measures the performance of the trader
     */
    public Trader(final String nameInput, final Account accountInput, final IExchange exchangeInput,
	    final TradingRulePerceptron buyRuleInput, final TradingRulePerceptron sellRuleInput,
	    final ExchangablePair exchangablesToTradeInput, final TradingPerformance performanceInput) {
	super();
	this.setName(nameInput);
	this.setAccount(accountInput);
	this.setExchange(exchangeInput);
	this.buyRule = buyRuleInput;
	this.sellRule = sellRuleInput;
	this.setExchangablesToTrade(exchangablesToTradeInput);
	this.setPerformance(performanceInput);
    }

    @Override
    public final double calculateFitness() {
	double result = performance.getNetProfitPercent();
	if (performance.getNumberOfTrades() == 0) {
	    result = -1;
	}
	return result;
    }

    @Override
    public final void performAction() {
	trade();

    }

    /**
     * performs either buy or sell order depending on the result of the
     * perceptrons and the currently possessed exchangables in the account.
     */
    private void trade() {
	double currentExchangeRate = getExchange().getCurrentExchangeRate(getExchangablesToTrade()).getToPrice();
	final int hoursPerDay = 24;

	if (currentExchangeRate != lastSeenRate && !areOrdersPending()) {
	    ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	    ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());

	    if (from.getAmount() > 0 && buyRule
		    .isActivated(getExchange().getExchangeRateHistory(getExchangablesToTrade(), hoursPerDay))) {
		buy();
	    } else if (to.getAmount() > 0 && sellRule
		    .isActivated(getExchange().getExchangeRateHistory(getExchangablesToTrade(), hoursPerDay))) {
		sell();
	    }
	}
	lastSeenRate = currentExchangeRate;

    }

    /**
     * Checks whether there are still orders pending. For this the orders
     * currently in the order book are checked.
     * 
     * @return returns true if there is at least one order of this trader in the
     *         order book.
     */
    private boolean areOrdersPending() {

	return exchange.getCurrentOrders(this).size() > 0;
    }

    /**
     * Generates a buy order.
     */
    private void buy() {
	ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	Order order = new Order(getExchangablesToTrade(), from.getAmount(), OrderType.BUY);
	ExchangableSet returnOfInvestment = getExchange().performOrder(order, this);

	getAccount().withdraw(new ExchangableSet(order.getPair().getFrom(), order.getAmount()));
	getAccount()
		.deposit(new ExchangableAmount(returnOfInvestment, order.getAmount() / returnOfInvestment.getAmount()));

	printAccountInfo();
	getPerformance().update(this, order);
    }

    /**
     * Generates a buy order.
     */
    private void sell() {
	ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	Order order = new Order(getExchangablesToTrade(), to.getAmount(), OrderType.SELL);
	ExchangableSet returnOfInvestment = getExchange().performOrder(order, this);

	getAccount().withdraw(new ExchangableSet(order.getPair().getTo(), order.getAmount()));
	getAccount()
		.deposit(new ExchangableAmount(returnOfInvestment, order.getAmount() / returnOfInvestment.getAmount()));

	printAccountInfo();
	getPerformance().update(this, order);
    }

    /**
     * Prints the current content of the account.
     */
    private void printAccountInfo() {
	ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	LOGGER.log(Level.FINE, "ETH -> BTC: "
		+ ((AbstractExchangeImpl) getExchange()).getCurrentExchangeRate(getExchangablesToTrade()).getToPrice());
	from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	LOGGER.log(Level.FINE, "ETH: " + from.getAmount());
	LOGGER.log(Level.FINE, "BTC: " + to.getAmount());
    }

    public final Account getAccount() {
	return account;
    }

    public final void setAccount(final Account wallet) {
	this.account = wallet;
    }

    @Override
    public final String getName() {
	return name;
    }

    public final void setName(final String nameInput) {
	this.name = nameInput;
    }

    public final ExchangablePair getExchangablesToTrade() {
	return exchangablesToTrade;
    }

    public final void setExchangablesToTrade(final ExchangablePair exchangablesToTradeInput) {
	this.exchangablesToTrade = exchangablesToTradeInput;
    }

    public final IExchange getExchange() {
	return exchange;
    }

    public final void setExchange(final IExchange exchangeInput) {
	this.exchange = exchangeInput;
    }

    public final TradingPerformance getPerformance() {
	return performance;
    }

    public final void setPerformance(final TradingPerformance performanceInput) {
	this.performance = performanceInput;
    }

    @Override
    public final List<IIndividual> combineWith(final IIndividual individual) {
	Account wallet = new Account();
	Trader result1 = new Trader(name + "|" + individual.getName(), wallet, exchange, buyRule,
		sellRule.combineWith(((Trader) individual).sellRule), exchangablesToTrade,
		new TradingPerformance(null));
	result1.setName(result1.generateDescriptiveName());
	wallet = new Account();
	Trader result2 = new Trader(name + "|" + individual.getName(), wallet, exchange,
		buyRule.combineWith(((Trader) individual).buyRule), sellRule, exchangablesToTrade,
		new TradingPerformance(null));
	result2.setName(result2.generateDescriptiveName());
	List<IIndividual> result = new ArrayList<>();
	result.add(result1);
	result.add(result2);
	return result;

    }

    /**
     * Generate a name based on the structure of the perceptrons.
     * 
     * @return the name
     */
    public final String generateDescriptiveName() {
	String result = "b";
	for (Input i : buyRule.getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "_" + i.getRule().getThreshold() + "-"
		    + i.getWeight();
	}
	result += "#" + buyRule.getThreshold();
	result += "|s";
	for (Input i : sellRule.getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "_" + i.getRule().getThreshold() + "-"
		    + i.getWeight();
	}
	result += "#" + sellRule.getThreshold();

	return result;
    }

    @Override
    public final void mutate() {
	buyRule.setRandomThreshold();
	sellRule.setRandomThreshold();

    }

}
