package de.wieczorek.eot.domain.machine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachine extends AbstractMachine {

    private static final Logger logger = Logger.getLogger(VirtualMachine.class.getName());

    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(100);
    protected MyUI callback;

    public VirtualMachine(final IExchange exchange, final MyUI callback, final Population traders) {
	super(exchange, traders);
	this.callback = callback;

    }

    @Override
    public void start() {
	traders.getNextPopulation(100);
	final Runnable task2 = () -> {
	    callback.updateLabel(this.traders.getAll());
	};

	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(task2, 0, 1, TimeUnit.SECONDS);

	final Runnable task = () -> {

	    final SimulatedExchangeImpl exchange = (SimulatedExchangeImpl) getExchange();
	    exchange.setHistory(null);
	    exchange.getExchangeRateHistory(new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), 365 * 12);
	    final int cycles = exchange.getHistory().getCompleteHistoryData().size() - 15 * 60;
	    for (int i = 30 * 15; i < cycles; i += 15) {
		for (int n = 0; n < 15; n++) {
		    exchange.icrementTime();
		}

		final CountDownLatch latch = new CountDownLatch(this.traders.getAll().size());
		for (final Trader trader : this.traders.getAll()) {
		    final Runnable foo = () -> {
			trader.performAction();
			latch.countDown();
		    };
		    new Thread(foo).start();
		}
		try {
		    latch.await();
		} catch (final InterruptedException E) {
		    // TODO handle
		}

	    }

	    scheduler.shutdown();
	    callback.updateLabel(this.traders.getAll());
	};

	final Thread thread = new Thread(task);
	thread.start();

	logger.log(Level.INFO, "Done!");
    }

}
