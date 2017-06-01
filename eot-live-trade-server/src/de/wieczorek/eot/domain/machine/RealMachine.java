package de.wieczorek.eot.domain.machine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.inject.Singleton;

import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.IAccount;
import de.wieczorek.eot.domain.trader.SynchronizingAccount;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.domain.trader.TradingPerformance;
import de.wieczorek.eot.domain.trading.rule.ComparatorType;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.MacdGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.StochasticFastGraphMetric;
import de.wieczorek.eot.ui.rest.InjectorSingleton;

@Singleton
public class RealMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(RealMachine.class.getName());

    private ScheduledExecutorService service;
    private final int maxPopulations = 20;

    @Inject
    public RealMachine(final IExchange exchange, Population population) {
	super(exchange, population);

	Handler consoleHandler = new ConsoleHandler();
	consoleHandler.setLevel(Level.FINE);
	Logger.getAnonymousLogger().addHandler(consoleHandler);

	Logger rootLogger = Logger.getLogger("");
	for (Handler handler : rootLogger.getHandlers()) {
	    // Change log level of default handler(s) of root logger
	    // The paranoid would check that this is the ConsoleHandler ;)
	    handler.setLevel(Level.FINE);
	}
	// Set root logger level
	rootLogger.setLevel(Level.FINE);

	final SynchronizingAccount wallet = (SynchronizingAccount) InjectorSingleton.getInjector()
		.getInstance(IAccount.class);
	wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	AbstractGraphMetric macdMetric = new MacdGraphMetric();
	AbstractGraphMetric stochasticFastMetric = new StochasticFastGraphMetric();
	AbstractGraphMetric coppochMetric = new CoppocGraphMetric();

	final TradingRule buyRule1 = new TradingRule();
	buyRule1.setThreshold(46.0);
	buyRule1.setComparator(ComparatorType.GREATER);
	buyRule1.setMetric(stochasticFastMetric);

	final TradingRule buyRule2 = new TradingRule();
	buyRule2.setThreshold(-2.4000000000000004);
	buyRule2.setComparator(ComparatorType.EQUAL);
	buyRule2.setMetric(coppochMetric);

	final TradingRule buyRule3 = new TradingRule();
	buyRule3.setThreshold(-7.42685546875);
	buyRule3.setComparator(ComparatorType.LESS);
	buyRule3.setMetric(coppochMetric);

	final TradingRule buyRule4 = new TradingRule();
	buyRule4.setThreshold(-7.1134765625);
	buyRule4.setComparator(ComparatorType.LESS);
	buyRule4.setMetric(macdMetric);

	final TradingRule buyRule5 = new TradingRule();
	buyRule5.setThreshold(-6.7375);
	buyRule5.setComparator(ComparatorType.GREATER);
	buyRule5.setMetric(macdMetric);

	final TradingRulePerceptron buyPerceptron = new TradingRulePerceptron(buyRule1, 3.0, 81);
	buyPerceptron.add(buyRule2, 2.0);
	buyPerceptron.add(buyRule3, 29.0);
	buyPerceptron.add(buyRule4, 51.0);
	buyPerceptron.add(buyRule5, 5);

	final TradingRule sellRule1 = new TradingRule();
	sellRule1.setThreshold(2.4000000000000004 - 1.0);
	sellRule1.setComparator(ComparatorType.LESS);
	sellRule1.setMetric(coppochMetric);

	final TradingRule sellRule2 = new TradingRule();
	sellRule2.setThreshold(9.3);
	sellRule2.setComparator(ComparatorType.GREATER);
	sellRule2.setMetric(coppochMetric);

	final TradingRule sellRule3 = new TradingRule();
	sellRule3.setThreshold(5.73203125);
	sellRule3.setComparator(ComparatorType.GREATER);
	sellRule3.setMetric(macdMetric);

	final TradingRulePerceptron sellPerceptron = new TradingRulePerceptron(sellRule1, 1, 21);
	sellPerceptron.add(sellRule2, 1);
	sellPerceptron.add(sellRule3, 20);

	final Trader newTrader = new Trader("macd_" + -1 + "_" + 1, wallet, exchange, buyPerceptron, sellPerceptron,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	newTrader.setExchange(exchange);
	newTrader.setNumberOfObservedHours(7);
	newTrader.setName(newTrader.generateDescriptiveName());

	final SynchronizingAccount wallet2 = (SynchronizingAccount) InjectorSingleton.getInjector()
		.getInstance(IAccount.class);
	wallet2.deposit(new ExchangableSet(ExchangableType.BTC, 1));

	final TradingRule buyRule6 = new TradingRule();
	buyRule6.setThreshold(-1.1);
	buyRule6.setComparator(ComparatorType.LESS);
	buyRule6.setMetric(macdMetric);

	final TradingRule sellRule4 = new TradingRule();
	sellRule4.setThreshold(1.1);
	sellRule4.setComparator(ComparatorType.GREATER);
	sellRule4.setMetric(macdMetric);

	final TradingRulePerceptron buyPerceptron2 = new TradingRulePerceptron(buyRule6, 1, 1);
	final TradingRulePerceptron sellPerceptron2 = new TradingRulePerceptron(sellRule4, 1, 1);

	final Trader newTrader2 = new Trader("macd_" + -1 + "_" + 1, wallet2, exchange, buyPerceptron2, sellPerceptron2,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	newTrader2.setExchange(exchange);
	newTrader2.setNumberOfObservedHours(1);
	newTrader2.setName(newTrader.generateDescriptiveName());

	// this.addTrader(newTrader);
	this.addTrader(newTrader2);
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
