package de.wieczorek.eot.domain.machine;

import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
import de.wieczorek.eot.domain.trading.rule.TraderNeuralNetwork;
import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.TradingRulePerceptron;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.MacdGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.StochasticFastGraphMetric;
import de.wieczorek.eot.ui.rest.InjectorSingleton;

@Singleton
public class RealMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(RealMachine.class.getName());

    private ScheduledExecutorService service;
    private final int maxPopulations = 20;
    private ScheduledFuture<?> future;

    @Inject
    public RealMachine(final IExchange exchange, Population population) {
	super(exchange, population);

	final SynchronizingAccount wallet = (SynchronizingAccount) InjectorSingleton.getInjector()
		.getInstance(IAccount.class);
	wallet.deposit(new ExchangableSet(ExchangableType.BTC, 1));
	final TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));
	final TradingPerformance performance2 = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1));

	AbstractGraphMetric macdMetric = new MacdGraphMetric();
	AbstractGraphMetric stochasticFastMetric = new StochasticFastGraphMetric();
	AbstractGraphMetric coppochMetric = new CoppocGraphMetric();
	AbstractGraphMetric rsiMetric = new RsiGraphMetric();

	final TradingRule buyRule10 = new TradingRule();
	buyRule10.setThreshold(-9.0);
	buyRule10.setComparator(ComparatorType.LESS);
	buyRule10.setMetric(coppochMetric);

	final TradingRule buyRule20 = new TradingRule();
	buyRule20.setThreshold(-6.9);
	buyRule20.setComparator(ComparatorType.LESS);
	buyRule20.setMetric(coppochMetric);

	final TradingRule buyRule21 = new TradingRule();
	buyRule21.setThreshold(-0.40000000000000036);
	buyRule21.setComparator(ComparatorType.LESS);
	buyRule21.setMetric(macdMetric);

	final TradingRule sellRule10 = new TradingRule();
	sellRule10.setThreshold(9.0);
	sellRule10.setComparator(ComparatorType.GREATER);
	sellRule10.setMetric(coppochMetric);

	final TradingRule sellRule11 = new TradingRule();
	sellRule11.setThreshold(0.40000000000000036);
	sellRule11.setComparator(ComparatorType.GREATER);
	sellRule11.setMetric(macdMetric);

	final TradingRule sellRule20 = new TradingRule();
	sellRule20.setThreshold(0.40000000000000036);
	sellRule20.setComparator(ComparatorType.GREATER);
	sellRule20.setMetric(macdMetric);

	final TradingRulePerceptron buyPerceptron1 = new TradingRulePerceptron(buyRule10, 1, 1, 491);

	final TradingRulePerceptron buyPerceptron2 = new TradingRulePerceptron(buyRule20, 1, 3, 491);
	buyPerceptron2.add(buyRule21, 2);

	final TradingRulePerceptron sellPerceptron1 = new TradingRulePerceptron(sellRule10, 1, 2, 491);
	sellPerceptron1.add(sellRule11, 1);

	final TradingRulePerceptron sellPerceptron2 = new TradingRulePerceptron(sellRule20, 1, 1, 491);

	final SynchronizingAccount wallet3 = (SynchronizingAccount) InjectorSingleton.getInjector()
		.getInstance(IAccount.class);
	wallet3.deposit(new ExchangableSet(ExchangableType.BTC, 1));

	final Trader newTrader3 = new Trader(wallet3, exchange, new TraderNeuralNetwork(buyPerceptron1, buyPerceptron2),
		new TraderNeuralNetwork(sellPerceptron1, sellPerceptron2),
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	newTrader3.setExchange(exchange);
	newTrader3.setNumberOfChunks(4);

	this.addTrader(newTrader3);
	// #this.addTrader(newTrader2);

	Enumeration<String> loggers = LogManager.getLogManager().getLoggerNames();
	while (loggers.hasMoreElements()) {

	    String nextLoggerName = loggers.nextElement();
	    System.out.println(nextLoggerName);
	    if (nextLoggerName.startsWith("de.wieczorek.eot"))
		LogManager.getLogManager().getLogger(nextLoggerName).setLevel(Level.ALL);
	    else
		LogManager.getLogManager().getLogger(nextLoggerName).setLevel(Level.OFF);
	}
    }

    @Override
    public void start() {
	logger.log(Level.FINE, "started real machine");
	service = Executors.newSingleThreadScheduledExecutor();
	service.scheduleAtFixedRate(this::triggerAllIndividuals, 0, 1, TimeUnit.MINUTES);
	this.state = MachineState.STARTED;
    }

    private void triggerAllIndividuals() {
	logger.log(Level.FINE, "Triggering individuals");
	try {
	    this.getTraders().getAll().stream().forEach(individual -> individual.performAction());
	} catch (Exception e) {
	    e.printStackTrace();
	}
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
