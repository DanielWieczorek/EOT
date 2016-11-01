package de.wieczorek.eot.business.configuration.simulation;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class SimulationConfigurationUcImpl implements ISimulationConfigurationUc {

    private Configuration simulationConfiguration;

    public SimulationConfigurationUcImpl() {
	final Configurations configs = new Configurations();
	try {
	    simulationConfiguration = configs.properties(new File("src/ main/simulation.properties"));

	} catch (final ConfigurationException cex) {
	    // TODO: log exception
	    cex.printStackTrace();
	    simulationConfiguration = null;
	}
    }

    @Override
    public double getOrderFees() {
	return simulationConfiguration.getDouble("simulation.order.fee");
    }

    @Override
    public int getOrderExecutionTime() {
	return simulationConfiguration.getInt("simulation.order.time");
    }
}
