package de.wieczorek.eot.domain.evolution;

import java.util.List;

/**
 * Represents a current population of individuals.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IPopulation {
    /**
     * Adds an individual the to the population.
     * 
     * @param individual
     *            the individual to add
     */
    void add(IIndividual individual);

    /**
     * returns all individuals of the population.
     * 
     * @return a list of individuals
     */
    List<IIndividual> getAll();

    /**
     * Generates a new generation from the current generation which shall have
     * at maximum the desired size.
     * 
     * @param size
     *            the desired size of the next population
     */
    void getNextPopulation(int size);

    /**
     * Removes all individuals from the population.
     */
    void clearPopulation();

    /**
     * Get the number of the current generation.
     * 
     * @return the number of the current generation.
     */
    int getPopulationNumber();
}
