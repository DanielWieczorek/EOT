package de.wieczorek.eot.domain.machine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.inject.Singleton;

import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Account;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trading.rule.ComparatorType;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;

@Singleton
public class RealMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(RealMachine.class.getName());

    private ScheduledExecutorService service;
    private final int maxPopulations = 20;

    @Inject
    public RealMachine(final IExchange exchange, Population population) {
	super(exchange, population);
	Logger rootLogger = Logger.getLogger("");
	for (Handler handler : rootLogger.getHandlers()) {
	    // Change log level of default handler(s) of root logger
	    // The paranoid would check that this is the ConsoleHandler ;)
	    handler.setLevel(Level.ALL);
	}
	// Set root logger level
	rootLogger.setLevel(Level.ALL);
	System.setProperty("java.util.logging.SimpleFormatter.format",
		"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

	final Account wallet = new Account();
	wallet.deposit(new ExchangableAmount(new ExchangableSet(ExchangableType.BTC, 1), 0));
	final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	final TradingRule rule1 = new TradingRule();
	rule1.setThreshold(50);
	rule1.setComparator(ComparatorType.LESS);
	AbstractGraphMetric metric1 = new RsiGraphMetric();
	// metric1.setStrategy(ExecutionLocationStrategy.CPU_ONLY);
	rule1.setMetric(metric1);

	final TradingRule rule12 = new TradingRule();
	rule12.setThreshold(50);
	rule12.setComparator(ComparatorType.GREATER);
	// metric1.setStrategy(ExecutionLocationStrategy.CPU_ONLY);
	rule12.setMetric(metric1);

	final TradingRulePerceptron buyRule = new TradingRulePerceptron(rule1, 1, 1);
	buyRule.add(rule12, 1.0);
	final TradingRulePerceptron sellRule = new TradingRulePerceptron(rule1, 1, 1);
	sellRule.add(rule12, 1.0);
	final Trader newTrader = new Trader("rsi_" + 50 + "_" + 50, wallet, exchange, buyRule, sellRule,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	newTrader.setExchange(exchange);
	newTrader.setNumberOfObservedHours(1);
	newTrader.setName(newTrader.generateDescriptiveName());

	this.addTrader(newTrader);
    }

    @Override
    public void start() {
	logger.log(Level.FINE, "started real machine");
	service = Executors.newSingleThreadScheduledExecutor();
	service.scheduleAtFixedRate(this::triggerAllIndividuals, 0, 1, TimeUnit.MINUTES);

    }

    private void triggerAllIndividuals() {
	logger.log(Level.FINE, "Triggering individuals");
	this.getTraders().getAll().stream().forEach(individual -> individual.performAction());
    }

    @Override
    public void pause() {
	logger.log(Level.FINE, "paused real machine");
	this.state = MachineState.PAUSED;
	service.shutdown();
    }

    @Override
    public void stop() {
	logger.log(Level.FINE, "stopped real machine");
	this.state = MachineState.STOPPED;
	service.shutdown();
    }

    public int getMaxPopulations() {
	return maxPopulations;
    }
}
