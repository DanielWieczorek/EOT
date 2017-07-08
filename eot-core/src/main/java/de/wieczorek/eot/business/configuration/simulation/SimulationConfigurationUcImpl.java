package de.wieczorek.eot.business.configuration.simulation;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * implementation of {@link ISimulationConfigurationUc}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class SimulationConfigurationUcImpl implements ISimulationConfigurationUc {

    /**
     * Object for accessing the configuration.
     */
    private Configuration simulationConfiguration;

    /**
     * Constructor.
     */
    public SimulationConfigurationUcImpl() {
	final Configurations configs = new Configurations();
	try {
	    simulationConfiguration = configs.properties("simulation.properties");

	} catch (final ConfigurationException cex) {
	    // TODO: log exception
	    cex.printStackTrace();
	    simulationConfiguration = null;
	}
    }

    @Override
    public final double getOrderFees() {
	return simulationConfiguration.getDouble("simulation.order.fee");
    }

    @Override
    public final int getOrderExecutionTime() {
	return simulationConfiguration.getInt("simulation.order.time");
    }
}
