package de.wieczorek.eot.business.configuration.exchange;

import com.google.inject.AbstractModule;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ExchangeConfigurationUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IExchangeConfigurationUc.class).to(ExchangeConfigurationUcImpl.class);

    }

}
