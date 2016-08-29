package de.wieczorek.eot.domain.exchange;

public class AbstractExchangeBuilder {

	public IExchange createSimulatedExchange() {
		
		return new SimulatedExchangeBuilder().createExchange();
	}

}
