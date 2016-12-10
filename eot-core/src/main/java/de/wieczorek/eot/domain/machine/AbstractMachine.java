package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.business.IBusinessLayerFacade;
import de.wieczorek.eot.domain.evolution.IPopulation;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Trader;

public abstract class AbstractMachine {

    protected IExchange exchange;
    private IPopulation traders;
    protected MachineState state;
    protected IBusinessLayerFacade businessLayer;

    public AbstractMachine(final IExchange exchange, IPopulation traders, IBusinessLayerFacade businessLayer) {
	this.traders = traders;
	this.exchange = exchange;
	this.businessLayer = businessLayer;
	this.state = MachineState.STOPPED;
    }

    public IExchange getExchange() {
	return exchange;
    }

    public abstract void start();

    public abstract void pause();

    public abstract void stop();

    public void addTrader(Trader trader) {
	getTraders().add(trader);
    }

    public IPopulation getTraders() {
	return traders;
    }

    public MachineState getState() {
	return state;
    }

}
