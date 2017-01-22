package de.wieczorek.eot.business.configuration;

import javax.inject.Inject;

import de.wieczorek.eot.business.configuration.simulation.ISimulationConfigurationUc;

/**
 * implementation of {@link IConfigurationUc}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ConfigurationUcImpl implements IConfigurationUc {

    /**
     * Configuration regarding the configuration for the simulation.
     */
    private ISimulationConfigurationUc simulationConf;

    /**
     * Constructor.
     * 
     * @param simulationConfInput
     *            configuration of the simulation.
     */
    @Inject
    public ConfigurationUcImpl(final ISimulationConfigurationUc simulationConfInput) {
	this.simulationConf = simulationConfInput;
    }

    @Override
    public final double getOrderFees() {
	return simulationConf.getOrderFees();
    }

    @Override
    public final int getOrderExecutionTime() {
	return simulationConf.getOrderExecutionTime();
    }
}
