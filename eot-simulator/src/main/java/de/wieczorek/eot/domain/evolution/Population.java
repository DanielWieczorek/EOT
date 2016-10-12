package de.wieczorek.eot.domain.evolution;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import de.wieczorek.eot.domain.trader.Trader;

public class Population implements IPopulation {

    private List<Trader> currentGeneration;
    private final EvolutionEngine engine;

    @Inject
    public Population(final EvolutionEngine engine) {
	currentGeneration = new LinkedList<>();
	this.engine = engine;
    }

    @Override
    public void add(final Trader trader) {
	currentGeneration.add(trader);
    }

    @Override
    public List<Trader> getAll() {
	return currentGeneration;
    }

    @Override
    public void getNextPopulation(final int size) {
	// if (currentGeneration.isEmpty())
	currentGeneration = engine.getInitialPopulation(size);
	// else
	// currentGeneration = engine.getNextPopulation(size,
	// currentGeneration);
    }

}
