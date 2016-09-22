package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.AbstractExchangeBuilder;
import de.wieczorek.eot.ui.MyUI;

public class VirtualMachineBuilder {

	private AbstractExchangeBuilder exchangeBuilder;

	public AbstractMachine createMachine(MyUI callback) {
		VirtualMachine vm = new VirtualMachine(exchangeBuilder.createSimulatedExchange(), callback);
		return vm;
	}

	public VirtualMachineBuilder(AbstractExchangeBuilder exchangeBuilder) {
		this.exchangeBuilder = exchangeBuilder;
	}

}
