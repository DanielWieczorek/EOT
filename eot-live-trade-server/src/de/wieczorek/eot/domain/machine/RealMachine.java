package de.wieczorek.eot.domain.machine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
	buyRule10.setThreshold(3.5645843346913653);
	buyRule10.setComparator(ComparatorType.GREATER);
	buyRule10.setMetric(stochasticFastMetric);

	final TradingRule buyRule11 = new TradingRule();
	buyRule11.setThreshold(-8.31146240234375);
	buyRule11.setComparator(ComparatorType.EQUAL);
	buyRule11.setMetric(macdMetric);

	final TradingRule buyRule12 = new TradingRule();
	buyRule12.setThreshold(-9.798828125);
	buyRule12.setComparator(ComparatorType.LESS);
	buyRule12.setMetric(coppochMetric);

	final TradingRule buyRule13 = new TradingRule();
	buyRule13.setThreshold(17.51402791341146);
	buyRule13.setComparator(ComparatorType.LESS);
	buyRule13.setMetric(rsiMetric);

	final TradingRule buyRule14 = new TradingRule();
	buyRule14.setThreshold(-8.585250409444171);
	buyRule14.setComparator(ComparatorType.LESS);
	buyRule14.setMetric(macdMetric);

	final TradingRule buyRule15 = new TradingRule();
	buyRule15.setThreshold(3.703125);
	buyRule15.setComparator(ComparatorType.LESS);
	buyRule15.setMetric(stochasticFastMetric);

	final TradingRule buyRule16 = new TradingRule();
	buyRule16.setThreshold(-9.796340942382812);
	buyRule16.setComparator(ComparatorType.GREATER);
	buyRule16.setMetric(coppochMetric);

	final TradingRule buyRule17 = new TradingRule();
	buyRule17.setThreshold(20.0);
	buyRule17.setComparator(ComparatorType.GREATER);
	buyRule17.setMetric(rsiMetric);

	final TradingRule buyRule18 = new TradingRule();
	buyRule18.setThreshold(-8.88683738708496);
	buyRule18.setComparator(ComparatorType.GREATER);
	buyRule18.setMetric(macdMetric);

	final TradingRule sellRule10 = new TradingRule();
	sellRule10.setThreshold(96.72565877437592);
	sellRule10.setComparator(ComparatorType.GREATER);
	sellRule10.setMetric(stochasticFastMetric);

	final TradingRule sellRule11 = new TradingRule();
	sellRule11.setThreshold(7.9);
	sellRule11.setComparator(ComparatorType.GREATER);
	sellRule11.setMetric(coppochMetric);

	final TradingRule sellRule12 = new TradingRule();
	sellRule12.setThreshold(8.64804103448987);
	sellRule12.setComparator(ComparatorType.GREATER);
	sellRule12.setMetric(macdMetric);

	final TradingRulePerceptron buyPerceptron3 = new TradingRulePerceptron(buyRule10, 5883, 10634);
	final TradingRulePerceptron sellPerceptron3 = new TradingRulePerceptron(sellRule10, 8588, 47710);

	buyPerceptron3.add(buyRule11, 79);
	buyPerceptron3.add(buyRule12, 112);
	buyPerceptron3.add(buyRule13, 629);
	buyPerceptron3.add(buyRule14, 2527);
	buyPerceptron3.add(buyRule15, 11);
	buyPerceptron3.add(buyRule16, 1054);
	buyPerceptron3.add(buyRule17, 846);
	buyPerceptron3.add(buyRule18, 10634);

	sellPerceptron3.add(sellRule11, 4206);
	sellPerceptron3.add(sellRule12, 34916);

	final SynchronizingAccount wallet3 = (SynchronizingAccount) InjectorSingleton.getInjector()
		.getInstance(IAccount.class);
	wallet3.deposit(new ExchangableSet(ExchangableType.BTC, 1));

	final Trader newTrader3 = new Trader("macd_" + -1 + "_" + 1, wallet3, exchange, buyPerceptron3, sellPerceptron3,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);
	newTrader3.setExchange(exchange);
	newTrader3.setNumberOfObservedMinutes(1651);
	newTrader3.setName(newTrader3.generateDescriptiveName());

	final TradingRule buyRule20 = new TradingRule();
	buyRule20.setThreshold(-6.15);
	buyRule20.setComparator(ComparatorType.LESS);
	buyRule20.setMetric(macdMetric);

	final TradingRule sellRule20 = new TradingRule();
	sellRule20.setThreshold(7.1);
	sellRule20.setComparator(ComparatorType.GREATER);
	sellRule20.setMetric(macdMetric);

	final TradingRulePerceptron buyPerceptron2 = new TradingRulePerceptron(buyRule20, 1, 1);
	final TradingRulePerceptron sellPerceptron2 = new TradingRulePerceptron(sellRule20, 1, 1);

	final SynchronizingAccount wallet2 = (SynchronizingAccount) InjectorSingleton.getInjector()
		.getInstance(IAccount.class);
	wallet2.deposit(new ExchangableSet(ExchangableType.BTC, 1));

	final Trader newTrader2 = new Trader("macd_" + -1 + "_" + 1, wallet2, exchange, buyPerceptron2, sellPerceptron2,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance2);
	newTrader2.setExchange(exchange);
	newTrader2.setNumberOfObservedMinutes(11);
	newTrader2.setName(newTrader3.generateDescriptiveName());

	this.addTrader(newTrader3);
	// #this.addTrader(newTrader2);
    }

    @Override
    public void start() {
	logger.log(Level.FINE, "started real machine");
	service = Executors.newSingleThreadScheduledExecutor();
	service.scheduleAtFixedRate(this::triggerAllIndividuals, 0, 1, TimeUnit.MINUTES);

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
