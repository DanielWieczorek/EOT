package de.wieczorek.eot.business.configuration.simulation;

/**
 * Usecase for reading and writing the configuration regarding the simulation.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface ISimulationConfigurationUc {

    /**
     * reads the transaction fees for the simulation from the configuration.
     * 
     * @return the transaction fee
     */
    double getOrderFees();

    /**
     * reads the order execution time from the configuration.
     * 
     * @return the order execution time.
     */
    int getOrderExecutionTime();
}
