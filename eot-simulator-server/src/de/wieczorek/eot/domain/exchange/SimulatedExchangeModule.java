package de.wieczorek.eot.domain.exchange;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;

public class SimulatedExchangeModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IExchange.class).to(SimulatedExchangeImpl.class);
    }

}
