package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.evolution.IPopulation;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.trader.Trader;

/**
 * Abstract superclass for the class which handles the execution of the domain
 * logic. The naming was done in reference to the say of Frank Schirrmacher who
 * said that "we are living in the machine".
 * 
 * @author Daniel Wieczorek
 *
 */
public abstract class AbstractMachine {

    /**
     * The exchange on which is being traded. Needed for initialization.
     */
    protected IExchange exchange;
    /**
     * The population of individuals which are trading at the exchange.
     */
    private IPopulation traders;

    /**
     * The current state of the machine, e.g. running, paused, stopped.
     */
    protected MachineState state;

    /**
     * Constructor.
     * 
     * @param exchangeInput
     *            the exchange for this machine
     * @param indiviuals
     *            the individuals this machine is working with
     */
    public AbstractMachine(final IExchange exchangeInput, final IPopulation indiviuals) {
	this.traders = indiviuals;
	this.exchange = exchangeInput;
	this.state = MachineState.STOPPED;
    }

    public final IExchange getExchange() {
	return exchange;
    }

    /**
     * Starts the machine.
     */
    public abstract void start();

    /**
     * Pauses the machine, so that it can resume at the same location it was
     * stopped.
     */
    public abstract void pause();

    /**
     * Stops the machine. If started again the machine wil start from the
     * beginning.
     */
    public abstract void stop();

    /**
     * Adds a trader to the machines population.
     * 
     * @param trader
     *            the trader to add
     */
    public final void addTrader(final Trader trader) {
	getTraders().add(trader);
    }

    public final IPopulation getTraders() {
	return traders;
    }

    public final MachineState getState() {
	return state;
    }

}
