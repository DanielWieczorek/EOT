package de.wieczorek.eot.domain.evolution;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class Population implements IPopulation {

    private List<IIndividual> currentGeneration;
    private final EvolutionEngine engine;

    @Inject
    public Population(final EvolutionEngine engine) {
	currentGeneration = new LinkedList<>();
	this.engine = engine;
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
	} else {
	    currentGeneration = engine.getNextPopulation(size, getBestIndividuals(20));
	}
    }

    private List<IIndividual> getBestIndividuals(final int amount) {
	final Comparator<IIndividual> byRating = (e1, e2) -> Double.compare(e2.calculateFitness(),
		e1.calculateFitness());

	final List<IIndividual> result = currentGeneration.stream().sorted(byRating).limit(amount)
		.collect(Collectors.toList());
	return result;
    }

}
