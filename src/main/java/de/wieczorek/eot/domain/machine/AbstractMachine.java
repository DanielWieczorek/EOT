package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.exchange.IExchange;

public abstract class AbstractMachine {

protected IExchange exchange;
	
	public AbstractMachine(IExchange exchange){
		this.exchange = exchange;
	}

	public IExchange getExchange() {
		return exchange;
	}
}
