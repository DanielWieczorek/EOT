package de.wieczorek.eot.business.price.impl;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.business.price.IExchangeRateUc;

public class ExchangeRateUcModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IExchangeRateUc.class).to(ExchangeRateUcImpl.class);

    }

}
