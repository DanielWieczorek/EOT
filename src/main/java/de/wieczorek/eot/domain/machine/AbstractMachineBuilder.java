package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.AbstractExchangeBuilder;

public class AbstractMachineBuilder {
	
	private AbstractExchangeBuilder exchangeBuilder;
	
	public AbstractMachineBuilder(){
		this.exchangeBuilder = new AbstractExchangeBuilder();
	}
	
	public AbstractMachine createVirtualMachine(){
		return new VirtualMachineBuilder(exchangeBuilder).createMachine();
	}

}
