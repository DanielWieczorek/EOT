package de.wieczorek.eot.business.configuration;

import com.google.inject.AbstractModule;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ConfigurationUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IConfigurationUc.class).to(ConfigurationUcImpl.class);

    }

}
