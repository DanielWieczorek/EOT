package de.wieczorek.eot.business.configuration;

import javax.inject.Inject;

import de.wieczorek.eot.business.configuration.simulation.ISimulationConfigurationUc;

public class ConfigurationUcImpl implements IConfigurationUc {

    private ISimulationConfigurationUc simulationConf;

    @Inject
    public ConfigurationUcImpl(ISimulationConfigurationUc simulationConf) {
	this.simulationConf = simulationConf;
    }

    @Override
    public double getOrderFees() {
	return simulationConf.getOrderFees();
    }

    @Override
    public int getOrderExecutionTime() {
	return simulationConf.getOrderExecutionTime();
    }
}
