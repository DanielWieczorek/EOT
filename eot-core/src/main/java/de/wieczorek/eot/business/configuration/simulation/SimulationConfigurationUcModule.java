package de.wieczorek.eot.business.configuration.simulation;

import com.google.inject.AbstractModule;

public class SimulationConfigurationUcModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(ISimulationConfigurationUc.class).to(SimulationConfigurationUcImpl.class);

    }

}
