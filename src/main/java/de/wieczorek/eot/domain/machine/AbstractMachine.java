package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.MyUI;

public abstract class AbstractMachine {

	protected IExchange exchange;
	protected Population traders;
	protected MyUI callback;

	public AbstractMachine(IExchange exchange, MyUI callback, Population traders) {
		this.exchange = exchange;
		this.callback = callback;
		this.traders = traders;
	}

	public AbstractMachine(IExchange exchange, Population traders) {
		this.exchange = exchange;
		this.traders = traders;
	}

	public IExchange getExchange() {
		return exchange;
	}

	public abstract void start();

	public void addTrader(Trader trader) {
		traders.add(trader);
	}
}
