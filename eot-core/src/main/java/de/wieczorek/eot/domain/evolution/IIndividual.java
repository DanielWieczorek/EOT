package de.wieczorek.eot.domain.evolution;

import java.util.List;

/**
 * Represents and individual in the context of the evolution engine. Individuals
 * can be combined with each other and have a measurable fitness.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IIndividual {

    /**
     * Calculates the fitness of the individual.
     * 
     * @return the fitness as double value
     */
    double calculateFitness();

    /**
     * Performs an action which affects its fitness.
     */
    void performAction();

    /**
     * Combines this individual with another individual. The result may be any
     * number of children.
     * 
     * @param iIndividual
     *            the individual to combine this indiviual with
     * @return the children
     */
    List<IIndividual> combineWith(IIndividual iIndividual);

    /**
     * Returns the name of the indiviual.
     * 
     * @return the name
     */
    String getName();

    /**
     * Changes the inidvidual.
     */
    void mutate();

    int getNumberOfTrades();

    double getNetProfit();
}
