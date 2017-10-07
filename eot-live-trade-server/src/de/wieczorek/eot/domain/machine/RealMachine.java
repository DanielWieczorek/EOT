package de.wieczorek.eot.domain.machine;

import java.io.StringReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.inject.Singleton;

import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.IAccount;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.rest.InjectorSingleton;
import de.wieczorek.eot.ui.trader.TraderConfiguration;
import de.wieczorek.eot.ui.trader.TraderFactory;

@Singleton
public class RealMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(RealMachine.class.getName());

    private ScheduledExecutorService service;
    private final int maxPopulations = 20;
    private ScheduledFuture<?> future;

    private String configuration = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><traderConfiguration><buyNetwork><perceptron1><inputs><comparator><binary>true</binary><threshold1>-2.5999999999999996</threshold1><threshold2>0.0</threshold2><type>LESS</type></comparator><type>MACD</type><weight>1.0</weight></inputs><inputs><comparator><binary>true</binary><threshold1>-3.1666666666666665</threshold1><threshold2>0.0</threshold2><type>LESS</type></comparator><type>Coppoch</type><weight>2.0</weight></inputs><observationTime>1584</observationTime><threshold>2.0</threshold></perceptron1><perceptron2><inputs><comparator><binary>true</binary><threshold1>-5.1</threshold1><threshold2>0.0</threshold2><type>LESS</type></comparator><type>Coppoch</type><weight>1.0</weight></inputs><observationTime>2673</observationTime><threshold>1.0</threshold></perceptron2><type>AND</type></buyNetwork><exchangablesToTrade><from>ETH</from><to>BTC</to></exchangablesToTrade><numberOfChunks>10</numberOfChunks><sellNetwork><perceptron1><inputs><comparator><binary>true</binary><threshold1>5.1</threshold1><threshold2>0.0</threshold2><type>GREATER</type></comparator><type>Coppoch</type><weight>1.0</weight></inputs><observationTime>2673</observationTime><threshold>1.0</threshold></perceptron1><perceptron2><inputs><comparator><binary>true</binary><threshold1>5.1</threshold1><threshold2>0.0</threshold2><type>GREATER</type></comparator><type>Coppoch</type><weight>1.0</weight></inputs><observationTime>2673</observationTime><threshold>1.0</threshold></perceptron2><type>AND</type></sellNetwork><stopLossActivated>false</stopLossActivated></traderConfiguration>";

    @Inject
    public RealMachine(final IExchange exchange, Population population) throws JAXBException {
	super(exchange, population);

	JAXBContext jaxbContext = JAXBContext.newInstance(TraderConfiguration.class);
	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

	StringReader reader = new StringReader(configuration);
	TraderConfiguration config = (TraderConfiguration) unmarshaller.unmarshal(reader);

	Trader trader = TraderFactory.createTrader(config, InjectorSingleton.getInjector().getInstance(IAccount.class),
		exchange);
	this.addTrader(trader);
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
