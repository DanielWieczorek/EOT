package de.wieczorek.eot.domain.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import de.wieczorek.eot.business.IBusinessLayerFacade;
import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(VirtualMachine.class.getName());

    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(numberOfExecutors);
    protected MyUI callback;
    private final int maxPopulations = 10;

    private final List<List<IIndividual>> traderGroups;

    private static final int numberOfExecutors = 8;

    private static final int populationSize = 100;

    @Inject
    public VirtualMachine(final IExchange exchange, final Population traders,
	    final IBusinessLayerFacade businessLayer) {
	super(exchange, traders, businessLayer);
	traderGroups = new ArrayList<>();

    }

    @Override
    public void start() {
	if (getState() == MachineState.STOPPED) {
	    this.state = MachineState.STARTED;
	    final Runnable task = () -> {
		getTraders().clearPopulation();
		final SimulatedExchangeImpl exchange = (SimulatedExchangeImpl) getExchange();
		exchange.setHistory(null);
		exchange.getExchangeRateHistory(new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC),
			365 * 12);

		for (int j = 0; j < maxPopulations && getState() != MachineState.STOPPED; j++) {
		    if (j == 0) {
			getTraders().getNextPopulation(populationSize);
		    } else {
			getTraders().getNextPopulation(getTraders().getAll().size() / 4);
		    }
		    exchange.reset();
		    final int cycles = exchange.getHistory().getCompleteHistoryData().size() - 15 * 60;
		    logger.info("running over " + cycles + " data points");
		    final long start = System.currentTimeMillis();
		    for (int i = 30 * 15; i < cycles && getState() != MachineState.STOPPED; i += 15) {
			while (getState() == MachineState.PAUSED) {
			    logger.info("simulation paused checking again in 10s");
			    try {
				Thread.sleep(10 * 1000);

			    } catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}

			for (int n = 0; n < 15; n++) {
			    exchange.icrementTime();
			}
			for (int n = 0; n < numberOfExecutors; n++) {
			    traderGroups.add(new ArrayList<>());
			}
			int n = 0;
			final CountDownLatch latch = new CountDownLatch(numberOfExecutors);
			for (final IIndividual trader : this.getTraders().getAll()) {
			    traderGroups.get(n).add(trader);
			    n++;
			    n %= numberOfExecutors;
			}

			final List<TraderGroupManager> managers = new ArrayList<>();

			for (n = 0; n < numberOfExecutors; n++) {
			    managers.add(new TraderGroupManager(traderGroups.get(n), latch));

			}

			for (n = 0; n < numberOfExecutors; n++) {
			    taskExecutor.execute(managers.get(n));
			}
			try {
			    latch.await();
			} catch (final InterruptedException E) {
			    E.printStackTrace();
			}

		    }
		    final long end = System.currentTimeMillis();
		    logger.info("Finished simulation of generation " + j + ". It took "
			    + (double) ((end - start) / 1000) + " seconds.");

		}

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
	private final List<IIndividual> individuals;
	private final CountDownLatch latch;

	public TraderGroupManager(final List<IIndividual> individuals, final CountDownLatch latch) {
	    this.individuals = individuals;
	    this.latch = latch;
	}

	@Override
	public void run() {
	    for (final IIndividual trader : individuals) {
		trader.performAction();
	    }
	    latch.countDown();
	}
    }

}
