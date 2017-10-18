package de.wieczorek.eot.domain.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
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

    private final long id;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Trader.class.getName());

    /**
     * account containing all the exchangables the trader possesses.
     */
    private IAccount account;

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

    // private int numberOfObservedMinutes = 24 * 60;

    private int numberOfChunks = 10;

    private TraderMemory buyMemory;
    private TraderMemory sellMemory;

    private int unsoldBuys = 0;

    private boolean isStopLossActivated;

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
    public Trader(final IAccount accountInput, final IExchange exchangeInput, final TraderNeuralNetwork buyRuleInput,
	    final TraderNeuralNetwork sellRuleInput, final ExchangablePair exchangablesToTradeInput,
	    final TradingPerformance performanceInput, boolean isStopLossActivated) {
	this.id = TraderIdGenerator.getNextId();
	this.setAccount(accountInput);
	this.setExchange(exchangeInput);
	this.buyRule = buyRuleInput;
	this.sellRule = sellRuleInput;
	this.setExchangablesToTrade(exchangablesToTradeInput);
	this.setPerformance(performanceInput);
	this.buyMemory = new TraderMemory();
	this.sellMemory = new TraderMemory();
	this.isStopLossActivated = isStopLossActivated;
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

	if (!areOrdersPending()) { // currentExchangeRate != lastSeenRate &&
	    ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	    LOGGER.info("from amount: " + from.getAmount() + " " + from.getExchangable().name());
	    ExchangeRateHistory history = getExchange().getExchangeRateHistory(getExchangablesToTrade(),
		    getHighestObservationTime());

	    if (from.getAmount() > 0
		    && (sellRule != null && sellRule.isActivated(history) || isStopLossTriggered(history))) {
		LOGGER.info("triggering sell order");
		sell(currentExchangeRate);
	    } else {
		ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
		LOGGER.info("to amount: " + to.getAmount() + " " + to.getExchangable().name());
		if (to.getAmount() > 0 && buyRule.isActivated(history)) {
		    LOGGER.info("triggering buy order");
		    buy(currentExchangeRate);
		}
	    }
	} else {
	    LOGGER.info("orders are pending -> not trading");
	}

	lastSeenRate = currentExchangeRate;

    }

    private int getHighestObservationTime() {
	int result = buyRule.getHighestObservationTime();
	if (sellRule != null) {
	    result = Math.max(result, sellRule.getHighestObservationTime());
	}
	return result;
    }

    private boolean isStopLossTriggered(ExchangeRateHistory history) {
	return isStopLossActivated && unsoldBuys > 0 && isBelowThreshold(history);
    }

    private boolean isBelowThreshold(ExchangeRateHistory history) {
	List<TimedExchangeRate> filtered = history.getCompleteHistoryData().stream()
		.filter(f -> f.getTime().isAfter(sellMemory.getLastOrderDate())
			|| f.getTime().isEqual(sellMemory.getLastOrderDate()))
		.collect(Collectors.toList());

	if (!filtered.isEmpty()) {
	    double max = filtered.stream().mapToDouble(f -> f.getToPrice()).max().getAsDouble();
	    double absoluteThreshold = 0.0005;
	    return history.getMostRecentExchangeRate().getToPrice() < Math
		    .max(sellMemory.getLastOrder().getPrice() - absoluteThreshold, (max - absoluteThreshold));
	} else {
	    return false;
	}

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
	sellMemory.setLastOrder(order);
	sellMemory.setLastOrderDate(exchange.getTime());
	unsoldBuys++;
    }

    private double computeAmountToTrade(double currentExchangeRate, OrderType type) {
	ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());

	double fullAmount = (to.getAmount() / currentExchangeRate + from.getAmount()) / (numberOfChunks);
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

	buyMemory.setLastOrder(order);
	buyMemory.setLastOrderDate(exchange.getTime());
	unsoldBuys--;
	unsoldBuys = Math.max(0, unsoldBuys);

    }

    /**
     * Prints the current content of the account.
     */
    private void printAccountInfo() {
	ExchangableSet from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	ExchangableSet to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	LOGGER.debug("ETH -> BTC: "
		+ ((AbstractExchangeImpl) getExchange()).getCurrentExchangeRate(getExchangablesToTrade()).getToPrice());
	from = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getFrom());
	to = getAccount().countAllExchangablesOfType(getExchangablesToTrade().getTo());
	LOGGER.debug("ETH: " + from.getAmount());
	LOGGER.debug("BTC: " + to.getAmount());
    }

    public final IAccount getAccount() {
	return account;
    }

    public final void setAccount(final IAccount wallet) {
	this.account = wallet;
    }

    @Override
    public final String getName() {
	return generateDescriptiveName();
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

	List<TraderNeuralNetwork> sellRules = new ArrayList<TraderNeuralNetwork>();
	sellRules.add(null);
	sellRules.add(((Trader) individual).sellRule);
	if (sellRule != null && ((Trader) individual).sellRule != null)
	    sellRules = sellRule.combineWith(((Trader) individual).sellRule);
	List<TraderNeuralNetwork> buyRules = buyRule.combineWith(((Trader) individual).buyRule);
	Account wallet = new Account();
	Trader result1 = new Trader(wallet, exchange, buyRules.get(1), sellRules.get(0), exchangablesToTrade,
		new TradingPerformance(null), isStopLossActivated);

	result1.setNumberOfChunks((this.numberOfChunks + ((Trader) individual).numberOfChunks) / 2);

	wallet = new Account();
	Trader result2 = new Trader(wallet, exchange, buyRules.get(0), sellRules.get(1), exchangablesToTrade,
		new TradingPerformance(null), isStopLossActivated);

	result1.setNumberOfChunks((this.numberOfChunks + ((Trader) individual).numberOfChunks) / 2);

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

	String result = ""; // this.numberOfObservedMinutes + "_n|";
	result += id + ": " + this.numberOfChunks + "_c|b";
	for (Input i : buyRule.getPerceptron1().getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "-"
		    + i.getRule().getComparator().printDescription() + "-" + i.getWeight();
	}
	result += "#t_" + buyRule.getPerceptron1().getThreshold();
	result += "#o_" + buyRule.getPerceptron1().getObservationTime();
	result += buyRule.getType().name();

	for (Input i : buyRule.getPerceptron2().getInputs()) {
	    result += "::" + i.getRule().getMetric().getType().name() + "-"
		    + i.getRule().getComparator().printDescription() + "-" + i.getWeight();
	}
	result += "#t_" + buyRule.getPerceptron2().getThreshold();
	result += "#o_" + buyRule.getPerceptron2().getObservationTime();
	if (sellRule != null) {
	    result += "|s";
	    for (Input i : sellRule.getPerceptron1().getInputs()) {
		result += "::" + i.getRule().getMetric().getType().name() + "-"
			+ i.getRule().getComparator().printDescription() + "-" + i.getWeight();
	    }
	    result += "#t_" + sellRule.getPerceptron1().getThreshold();
	    result += "#o_" + sellRule.getPerceptron1().getObservationTime();
	    result += buyRule.getType().name();

	    for (Input i : sellRule.getPerceptron2().getInputs()) {
		result += "::" + i.getRule().getMetric().getType().name() + "-"
			+ i.getRule().getComparator().printDescription() + "-" + i.getWeight();
	    }
	    result += "#t_" + sellRule.getPerceptron2().getThreshold();
	    result += "#o_" + sellRule.getPerceptron2().getObservationTime();
	}
	result += isStopLossActivated ? "_sl" : "";

	return result;
    }

    @Override
    public final void mutate() {
	buyRule.setRandomThreshold();

	buyRule.randomizeOneComparator();

	buyRule.randomizeNetworkType();
	if (sellRule != null) {
	    sellRule.randomizeOneComparator();
	    sellRule.setRandomThreshold();
	    sellRule.randomizeNetworkType();
	}
    }

    @Override
    public int getNumberOfTrades() {
	return performance.getNumberOfTrades();
    }

    @Override
    public double getNetProfit() {

	return performance.getNetProfit();
    }

    public int getNumberOfChunks() {
	return numberOfChunks;
    }

    public void setNumberOfChunks(int numberOfChunks) {
	this.numberOfChunks = numberOfChunks;
    }

    @Override
    public long getId() {
	return id;
    }

    public TraderNeuralNetwork getBuyRule() {
	return buyRule;
    }

    public TraderNeuralNetwork getSellRule() {
	return sellRule;
    }

    public boolean isStopLossActivated() {
	return isStopLossActivated;
    }

    public void setStopLossActivated(boolean isStopLossActivated) {
	this.isStopLossActivated = isStopLossActivated;
    }

}
