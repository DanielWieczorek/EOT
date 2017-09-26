package de.wieczorek.eot.domain.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.inject.Singleton;

import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;
import de.wieczorek.eot.domain.trader.Trader;

@Singleton
public class VirtualMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(VirtualMachine.class.getName());

    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(numberOfExecutors);
    private final int maxPopulations = 20;

    private final List<List<IIndividual>> traderGroups;

    private static final int numberOfExecutors = 8;

    private static final int populationSize = 100;

    private static final int resultPopulationSize = 20;

    @Inject
    public VirtualMachine(final IExchange exchange, final Population traders) {
	super(exchange, traders);

	traderGroups = new ArrayList<>();
	LogManager.getRootLogger().setLevel(Level.OFF);
    }

    @Override
    public void start() {
	if (getState() == MachineState.STOPPED) {
	    this.state = MachineState.STARTED;
	    final Runnable task = () -> {
		getTraders().clearPopulation();
		final SimulatedExchangeImpl exchange = (SimulatedExchangeImpl) getExchange();

		final List<TraderGroupManager> managers = new ArrayList<>();

		for (int n = 0; n < numberOfExecutors; n++) {
		    managers.add(new TraderGroupManager(null, null));
		}

		for (int n = 0; n < numberOfExecutors; n++) {
		    traderGroups.add(new ArrayList<>());
		}
		int initialPopulationSize = 0;
		List<IIndividual> lastGenerationOfPreviousRun = new ArrayList<>();
		while (getState() != MachineState.STOPPED) {
		    getTraders().clearPopulation();
		    for (int j = 0; j < maxPopulations && getState() != MachineState.STOPPED; j++) {
			if (j == 0) {
			    getTraders().getNextPopulation(populationSize);
			    getTraders().addAll(lastGenerationOfPreviousRun);
			    initialPopulationSize = getTraders().getAll().size();

			} else {
			    getTraders().getNextPopulation(Math.max(
				    getTraders().getAll().size() - initialPopulationSize / (maxPopulations - 1),
				    resultPopulationSize));
			}
			exchange.reset();

			exchange.setHistory(null);
			exchange.getExchangeRateHistory(new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC),
				31 * 24 * 60);

			final int cycles = exchange.getHistory().getCompleteHistoryData().size() - 15 * 60;
			logger.fatal("running over " + cycles + " data points for " + getTraders().getAll().size());
			final long start = System.currentTimeMillis();

			int n = 0;
			final int realNumberOfExecutors = Math.min(numberOfExecutors,
				this.getTraders().getAll().size());

			for (List<IIndividual> traderGroup : traderGroups) {
			    traderGroup.clear();
			}

			for (final IIndividual trader : this.getTraders().getAll()) {
			    traderGroups.get(n).add(trader);
			    n++;
			    n %= realNumberOfExecutors;
			}
			for (int i = 60 * 24; i < cycles && getState() != MachineState.STOPPED; i++) {

			    while (getState() == MachineState.PAUSED) {
				logger.info("simulation paused checking again in 10s");
				try {
				    Thread.sleep(10 * 1000);

				} catch (final InterruptedException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
			    }
			    if (i % 150 == 0) {
				logger.fatal("current cycle:" + i + " of " + cycles + " data points");
			    }

			    exchange.icrementTime();

			    final CountDownLatch latch = new CountDownLatch(realNumberOfExecutors);

			    for (int x = 0; x < realNumberOfExecutors; x++) {
				managers.get(x).setLatch(latch);
				managers.get(x).setIndividuals(traderGroups.get(x));
			    }

			    for (n = 0; n < realNumberOfExecutors; n++) {
				taskExecutor.execute(managers.get(n));
			    }
			    try {
				latch.await(1, TimeUnit.MINUTES);
			    } catch (final InterruptedException E) {
				E.printStackTrace();
			    }
			}
			final long end = System.currentTimeMillis();
			logger.fatal("Finished simulation of generation " + j + " with "
				+ this.getTraders().getAll().size() + " individuals with " + getNumberOfMetrics()
				+ " applied metrics. It took " + (double) ((end - start) / 1000) + " seconds.");
		    }

		    this.getTraders().printPopulationInfo();
		    lastGenerationOfPreviousRun.clear();
		    lastGenerationOfPreviousRun.addAll(getTraders().getAll());

		}
		this.state = MachineState.STOPPED;
	    };

	    final Thread thread = new Thread(task);
	    thread.start();

	    logger.log(Level.INFO, "Done!");
	} else if (getState() == MachineState.PAUSED) {
	    this.state = MachineState.STARTED;
	} else {
	    logger.log(Level.INFO, "Simulation already started");
	}
    }

    private long getNumberOfMetrics() {
	long count = 0;
	for (IIndividual individual : getTraders().getAll()) {
	    Trader trader = ((Trader) individual);
	    count += trader.getBuyRule().getPerceptron1().getInputs().size();
	    count += trader.getBuyRule().getPerceptron2().getInputs().size();

	    count += trader.getSellRule().getPerceptron1().getInputs().size();
	    count += trader.getSellRule().getPerceptron2().getInputs().size();
	}
	return count;
    }

    @Override
    public void pause() {
	this.state = MachineState.PAUSED;

    }

    @Override
    public void stop() {
	this.state = MachineState.STOPPED;

    }

    public int getMaxPopulations() {
	return maxPopulations;
    }

    public class TraderGroupManager implements Runnable {
	private List<IIndividual> individuals;
	private CountDownLatch latch;

	public TraderGroupManager(final List<IIndividual> individuals, final CountDownLatch latch) {
	    this.setIndividuals(individuals);
	    this.setLatch(latch);
	}

	@Override
	public void run() {
	    getIndividuals().stream().forEach(t -> t.performAction());

	    getLatch().countDown();
	}

	public List<IIndividual> getIndividuals() {
	    return individuals;
	}

	public void setIndividuals(List<IIndividual> individuals) {
	    this.individuals = individuals;
	}

	public CountDownLatch getLatch() {
	    return latch;
	}

	public void setLatch(CountDownLatch latch) {
	    this.latch = latch;
	}
    }

}
