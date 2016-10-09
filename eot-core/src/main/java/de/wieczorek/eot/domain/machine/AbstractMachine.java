package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.evolution.IPopulation;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Trader;

public abstract class AbstractMachine {

    protected IExchange exchange;
    protected IPopulation traders;

    public AbstractMachine(IExchange exchange, IPopulation traders) {
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
