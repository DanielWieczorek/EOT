package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.AbstractExchangeBuilder;

public class VirtualMachineBuilder {

	private AbstractExchangeBuilder exchangeBuilder;
	
	public AbstractMachine createMachine() {
		VirtualMachine vm = new VirtualMachine(exchangeBuilder.createSimulatedExchange());
		return vm;
	}
		
	public VirtualMachineBuilder(AbstractExchangeBuilder exchangeBuilder){
		this.exchangeBuilder = exchangeBuilder;
	}

}
