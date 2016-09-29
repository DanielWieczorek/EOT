package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.AbstractExchangeBuilder;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachineBuilder {

	private AbstractExchangeBuilder exchangeBuilder;

	public AbstractMachine createMachine(MyUI callback) {
		IExchange exchange = exchangeBuilder.createSimulatedExchange();
		VirtualMachine vm = new VirtualMachine(exchange, callback, new Population(new EvolutionEngine(exchange)));
		return vm;
	}

	public VirtualMachineBuilder(AbstractExchangeBuilder exchangeBuilder) {
		this.exchangeBuilder = exchangeBuilder;
	}

}
