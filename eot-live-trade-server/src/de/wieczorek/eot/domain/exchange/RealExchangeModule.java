package de.wieczorek.eot.domain.exchange;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.domain.exchange.impl.RealExchangeImpl;

public class RealExchangeModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IExchange.class).to(RealExchangeImpl.class);
    }

}
