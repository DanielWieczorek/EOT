package de.wieczorek.eot.business.configuration.simulation;

import com.google.inject.AbstractModule;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class SimulationConfigurationUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(ISimulationConfigurationUc.class).to(SimulationConfigurationUcImpl.class);

    }

}
