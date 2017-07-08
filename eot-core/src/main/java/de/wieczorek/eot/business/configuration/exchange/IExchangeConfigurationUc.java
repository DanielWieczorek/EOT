package de.wieczorek.eot.business.configuration.exchange;

/**
 * Usecase for reading and writing the configuration regarding the simulation.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IExchangeConfigurationUc {

    /**
     * reads the key for the access to the exchange from the configuration.
     * 
     * @return the transaction fee
     */
    String getKey();

    /**
     * reads the secret for the access to the exchange from the configuration.
     * 
     * @return the order execution time.
     */
    String getSecret();
}
