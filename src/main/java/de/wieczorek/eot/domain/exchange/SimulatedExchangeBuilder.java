package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;

public class SimulatedExchangeBuilder {

    public IExchange createExchange() {
	final SimulatedExchangeImpl exchange = new SimulatedExchangeImpl();
	return exchange;
    }

}
