package de.wieczorek.eot.domain.machine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.ExchangablePair;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachine extends AbstractMachine {

	private ExecutorService taskExecutor = Executors.newFixedThreadPool(100);

	public VirtualMachine(IExchange exchange, MyUI callback, Population traders) {
		super(exchange, callback, traders);

	}

	@Override
	public void start() {
		traders.getNextPopulation(100);
		Runnable task2 = () -> {
			callback.updateLabel(this.traders.getAll());
		};

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(task2, 0, 1, TimeUnit.SECONDS);

		Runnable task = () -> {

			SimulatedExchangeImpl exchange = (SimulatedExchangeImpl) getExchange();
			exchange.setHistory(null);
			exchange.getExchangeRateHistory(new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), 365 * 12);
			int cycles = exchange.getHistory().getCompleteHistoryData().size() - 15;
			for (int i = 30 * 15; i < cycles; i += 15) {
				for (int n = 0; n < 15; n++)
					exchange.icrementTime();

				CountDownLatch latch = new CountDownLatch(this.traders.getAll().size());
				for (Trader trader : this.traders.getAll()) {
					Runnable foo = () -> {
						trader.performAction();
						latch.countDown();
					};
					new Thread(foo).start();
				}
				try {
					latch.await();
				} catch (InterruptedException E) {
					// TODO handle
				}

			}

			scheduler.shutdown();
			callback.updateLabel(this.traders.getAll());
		};

		Thread thread = new Thread(task);
		thread.start();

		System.out.println("Done!");
	}

}
