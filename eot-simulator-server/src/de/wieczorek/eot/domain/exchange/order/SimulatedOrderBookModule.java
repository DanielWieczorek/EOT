package de.wieczorek.eot.domain.exchange.order;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.domain.exchange.order.impl.SimulatedOrderBookImpl;

public class SimulatedOrderBookModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IOrderBook.class).to(SimulatedOrderBookImpl.class);
    }

}
