package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.AbstractExchangeBuilder;
import de.wieczorek.eot.ui.MyUI;

public class AbstractMachineBuilder {

	private AbstractExchangeBuilder exchangeBuilder;

	public AbstractMachineBuilder() {
		this.exchangeBuilder = new AbstractExchangeBuilder();
	}

	public AbstractMachine createVirtualMachine(MyUI callback) {
		return new VirtualMachineBuilder(exchangeBuilder).createMachine(callback);
	}

}
