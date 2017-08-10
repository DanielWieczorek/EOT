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

    /**
     * UC for the configuration for the exchange access.
     */
    private IExchangeConfigurationUc exchangeConf;

    /**
     * Constructor.
     * 
     * @param simulationConfInput
     *            configuration of the simulation.
     * @param exchangeConfInput
     *            configuration of the exchange
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
    public final String getKey() {
	return exchangeConf.getKey();
    }

    @Override
    public final String getSecret() {
	return exchangeConf.getSecret();
    }
}
