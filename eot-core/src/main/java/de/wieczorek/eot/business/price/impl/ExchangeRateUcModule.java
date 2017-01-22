package de.wieczorek.eot.business.price.impl;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.business.price.IExchangeRateUc;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ExchangeRateUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IExchangeRateUc.class).to(ExchangeRateUcImpl.class);

    }

}
