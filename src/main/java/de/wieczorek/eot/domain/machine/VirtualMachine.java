package de.wieczorek.eot.domain.machine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.ExchangablePair;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachine extends AbstractMachine {

	public VirtualMachine(IExchange exchange, MyUI callback) {
		super(exchange, callback);

	}

	@Override
	public void start() {

		Runnable task2 = () -> {
			callback.updateLabel(
					"" + this.traders.get(0).getWallet().countAllExchangablesOfType(ExchangableType.BTC).getAmount(),
					"" + this.traders.get(0).getWallet().countAllExchangablesOfType(ExchangableType.ETH).getAmount());
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
				this.traders.get(0).performAction();

			}
			scheduler.shutdown();
			callback.updateLabel(
					"" + this.traders.get(0).getWallet().countAllExchangablesOfType(ExchangableType.BTC).getAmount(),
					"" + this.traders.get(0).getWallet().countAllExchangablesOfType(ExchangableType.ETH).getAmount());
		};

		Thread thread = new Thread(task);
		thread.start();

		System.out.println("Done!");
	}

}
