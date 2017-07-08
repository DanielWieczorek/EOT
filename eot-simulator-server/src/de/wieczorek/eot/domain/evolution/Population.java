package de.wieczorek.eot.domain.evolution;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class Population implements IPopulation {

    private List<IIndividual> currentGeneration;
    private final EvolutionEngine engine;
    private int populationNumber;

    private static final Logger logger = Logger.getLogger(Population.class.getName());

    @Inject
    public Population(final EvolutionEngine engine) {
	currentGeneration = new LinkedList<>();
	this.engine = engine;
	this.populationNumber = 0;
    }

    @Override
    public void add(final IIndividual trader) {
	currentGeneration.add(trader);
    }

    @Override
    public List<IIndividual> getAll() {
	return currentGeneration;
    }

    @Override
    public void getNextPopulation(final int size) {

	if (currentGeneration.isEmpty()) {
	    currentGeneration = engine.getInitialPopulation(size);
	    populationNumber = 1;
	} else {
	    currentGeneration = engine.getNextPopulation(size, getBestIndividuals(Math.max(1, size / 2)));
	    populationNumber++;
	}
    }

    private List<IIndividual> getBestIndividuals(final int amount) {
	final Comparator<IIndividual> byRating = (e1, e2) -> Double.compare(e2.calculateFitness(),
		e1.calculateFitness());

	final List<IIndividual> result = currentGeneration.stream().sorted(byRating).limit(amount)
		.collect(Collectors.toList());
	return result;
    }

    @Override
    public void clearPopulation() {
	currentGeneration.clear();
    }

    @Override
    public int getPopulationNumber() {

	return populationNumber;
    }

    @Override
    public void printPopulationInfo() {
	final Comparator<IIndividual> byRating = (e1, e2) -> Double.compare(e2.calculateFitness(),
		e1.calculateFitness());

	final List<IIndividual> ordered = currentGeneration.stream().sorted(byRating).collect(Collectors.toList());
	for (final IIndividual individual : ordered) {
	    logger.severe("" + individual.getName() + ": " + individual.calculateFitness());
	}

    }

}
