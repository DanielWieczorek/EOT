package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.evolution.EvolutionEngine;
import de.wieczorek.eot.domain.evolution.Population;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.SimulatedExchangeBuilder;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachineBuilder {

    private final SimulatedExchangeBuilder exchangeBuilder;

    public AbstractMachine createMachine(final MyUI callback) {
	final IExchange exchange = exchangeBuilder.createExchange();
	final VirtualMachine vm = new VirtualMachine(exchange, callback, new Population(new EvolutionEngine(exchange)));
	return vm;
    }

    public VirtualMachineBuilder(final SimulatedExchangeBuilder exchangeBuilder) {
	this.exchangeBuilder = exchangeBuilder;
    }

}
