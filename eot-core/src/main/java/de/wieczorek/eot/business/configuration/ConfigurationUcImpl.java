package de.wieczorek.eot.business.configuration;

import javax.inject.Inject;

import de.wieczorek.eot.business.configuration.exchange.IExchangeConfigurationUc;
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

    private IExchangeConfigurationUc exchangeConf;

    /**
     * Constructor.
     * 
     * @param simulationConfInput
     *            configuration of the simulation.
     */
    @Inject
    public ConfigurationUcImpl(final ISimulationConfigurationUc simulationConfInput,
	    final IExchangeConfigurationUc exchangeConfInput) {
	this.simulationConf = simulationConfInput;
	this.exchangeConf = exchangeConfInput;
    }

    @Override
    public final double getOrderFees() {
	return simulationConf.getOrderFees();
    }

    @Override
    public final int getOrderExecutionTime() {
	return simulationConf.getOrderExecutionTime();
    }

    @Override
    public String getKey() {
	return exchangeConf.getKey();
    }

    @Override
    public String getSecret() {
	return exchangeConf.getSecret();
    }
}
