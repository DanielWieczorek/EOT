package de.wieczorek.eot.domain.machine;

import java.util.LinkedList;
import java.util.List;

import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.MyUI;

public abstract class AbstractMachine {

	protected IExchange exchange;
	protected List<Trader> traders;
	protected MyUI callback;

	public AbstractMachine(IExchange exchange, MyUI callback) {
		this.exchange = exchange;
		this.traders = new LinkedList<>();
		this.callback = callback;
	}

	public AbstractMachine(IExchange exchange, List<Trader> traders) {
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
