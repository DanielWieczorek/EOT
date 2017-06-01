package de.wieczorek.eot.business.trade.impl;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.business.trade.ITradeUc;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class TradeUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(ITradeUc.class).to(TradeUcImpl.class);

    }

}
