package de.wieczorek.eot.business.configuration;

import com.google.inject.AbstractModule;

public class ConfigurationUcModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IConfigurationUc.class).to(ConfigurationUcImpl.class);

    }

}
