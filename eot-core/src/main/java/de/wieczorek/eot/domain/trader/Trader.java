package de.wieczorek.eot.domain.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.OrderType;
import de.wieczorek.eot.domain.exchange.impl.AbstractExchangeImpl;
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork;
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
    private IAccount account;

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
    private TraderNeuralNetwork buyRule;

    /**
     * Perceptron determining whether the trader should sell an exchangable.
     */
    private TraderNeuralNetwork sellRule;

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

    private int numberOfObservedMinutes = 24 * 60;

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
    public Trader(final String nameInput, final IAccount accountInput, final IExchange exchangeInput,
	    final TraderNeuralNetwork buyRuleInput, final TraderNeuralNetwork sellRuleInput,
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

	return performance.getPerformanceRating();
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

	if (currentExchangeRate != lastSeenRate && !areOrdersPending()) {
	    ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	    LOGGER.fine("from amount: " + from.getAmount() + " " + from.getExchangable().name());
	    if (from.getAmount() > 0 && sellRule.isActivated(
		    getExchange().getExchangeRateHistory(getExchangablesToTrade(), getNumberOfObservedMinutes()))) {
		LOGGER.fine("triggering sell order");
		sell(currentExchangeRate);
	    } else {
		ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
		LOGGER.fine("to amount: " + to.getAmount() + " " + to.getExchangable().name());
		if (to.getAmount() > 0 && buyRule.isActivated(
			getExchange().getExchangeRateHistory(getExchangablesToTrade(), getNumberOfObservedMinutes()))) {
		    // LOGGER.severe("triggering buy order");
		    buy(currentExchangeRate);
		}
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
     * 
     * @param currentExchangeRate
     */
    private void buy(double currentExchangeRate) {
	Order order = new Order(getExchangablesToTrade(), computeAmountToTrade(currentExchangeRate, OrderType.BUY),
		OrderType.BUY);
	order.setPrice(currentExchangeRate);
	ExchangableSet returnOfInvestment = getExchange().performOrder(order, this);

	getAccount().withdraw(new ExchangableSet(order.getPair().getTo(), order.getAmount()));
	getAccount().deposit(returnOfInvestment);

	printAccountInfo();
	getPerformance().update(this, order);
    }

    private double computeAmountToTrade(double currentExchangeRate, OrderType type) {
	ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());

	double fullAmount = (to.getAmount() / currentExchangeRate + from.getAmount()) / 10.0;
	if (OrderType.BUY.equals(type)) {
	    return Math.min(to.getAmount(), fullAmount * currentExchangeRate);
	} else {
	    return Math.min(from.getAmount(), fullAmount);
	}
    }

    /**
     * Generates a buy order.
     * 
     * @param currentExchangeRate
     */
    private void sell(double currentExchangeRate) {
	Order order = new Order(getExchangablesToTrade(), computeAmountToTrade(currentExchangeRate, OrderType.SELL),
		OrderType.SELL);
	order.setPrice(currentExchangeRate);
	ExchangableSet returnOfInvestment = getExchange().performOrder(order, this);

	getAccount().withdraw(new ExchangableSet(order.getPair().getFrom(), order.getAmount()));
	getAccount().deposit(returnOfInvestment);

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

    public final IAccount getAccount() {
	return account;
    }

    public final void setAccount(final IAccount wallet) {
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
	List<TraderNeuralNetwork> sellRules = sellRule.combineWith(((Trader) individual).sellRule);
	List<TraderNeuralNetwork> buyRules = buyRule.combineWith(((Trader) individual).buyRule);
	Account wallet = new Account();
	Trader result1 = new Trader(name + "|" + individual.getName(), wallet, exchange, buyRules.get(1),
		sellRules.get(0), exchangablesToTrade, new TradingPerformance(null));
	result1.setNumberOfObservedMinutes(
		(this.numberOfObservedMinutes + ((Trader) individual).numberOfObservedMinutes) / 2);
	result1.setName(result1.generateDescriptiveName());

	wallet = new Account();
	Trader result2 = new Trader(name + "|" + individual.getName(), wallet, exchange, buyRules.get(0),
		sellRules.get(1), exchangablesToTrade, new TradingPerformance(null));
	result2.setNumberOfObservedMinutes(
		(this.numberOfObservedMinutes + ((Trader) individual).numberOfObservedMinutes) / 2);
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
	String result = this.numberOfObservedMinutes + "b";
	for (Input i : buyRule.getPerceptron1().getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "-" + i.getRule().getComparator().name() + "_"
		    + i.getRule().getThreshold() + "-" + i.getWeight();
	}
	result += "#" + buyRule.getPerceptron1().getThreshold();
	result += "TYPE:" + buyRule.getType().name();

	for (Input i : buyRule.getPerceptron2().getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "-" + i.getRule().getComparator().name() + "_"
		    + i.getRule().getThreshold() + "-" + i.getWeight();
	}
	result += "#" + buyRule.getPerceptron2().getThreshold();
	result += "|s";
	for (Input i : sellRule.getPerceptron1().getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "-" + i.getRule().getComparator().name() + "_"
		    + i.getRule().getThreshold() + "-" + i.getWeight();
	}
	result += "#" + sellRule.getPerceptron1().getThreshold();
	result += "TYPE:" + buyRule.getType().name();

	result += "|s";
	for (Input i : sellRule.getPerceptron2().getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "-" + i.getRule().getComparator().name() + "_"
		    + i.getRule().getThreshold() + "-" + i.getWeight();
	}
	result += "#" + sellRule.getPerceptron2().getThreshold();

	return result;
    }

    @Override
    public final void mutate() {
	buyRule.setRandomThreshold();
	sellRule.setRandomThreshold();

	buyRule.randomizeOneComparator();
	sellRule.randomizeOneComparator();

	buyRule.randomizeNetworkType();
	sellRule.randomizeNetworkType();
    }

    public int getNumberOfObservedMinutes() {
	return numberOfObservedMinutes;
    }

    public void setNumberOfObservedMinutes(int numberOfObservedMinutes) {
	this.numberOfObservedMinutes = numberOfObservedMinutes;
    }

    @Override
    public int getNumberOfTrades() {
	return performance.getNumberOfTrades();
    }

    @Override
    public double getNetProfit() {

	return performance.getNetProfit();
    }

}
