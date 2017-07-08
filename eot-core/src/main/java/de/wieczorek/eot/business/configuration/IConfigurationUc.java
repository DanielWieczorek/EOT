package de.wieczorek.eot.business.configuration;

import de.wieczorek.eot.business.configuration.exchange.IExchangeConfigurationUc;
import de.wieczorek.eot.business.configuration.simulation.ISimulationConfigurationUc;

/**
 * Usecase which is the facade for all configuration use cases.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IConfigurationUc extends ISimulationConfigurationUc, IExchangeConfigurationUc {

}
