package de.wieczorek.eot.domain.machine;

import java.util.LinkedList;
import java.util.List;

import de.wieczorek.eot.domain.trader.Trader;

public class Population implements IPopulation {

	private List<Trader> currentGeneration;
	private EvolutionEngine engine;

	public Population(EvolutionEngine engine) {
		currentGeneration = new LinkedList<>();
		this.engine = engine;
	}

	@Override
	public void add(Trader trader) {
		currentGeneration.add(trader);
	}

	@Override
	public List<Trader> getAll() {
		return currentGeneration;
	}

	@Override
	public void getNextPopulation(int size) {
		// if (currentGeneration.isEmpty())
		currentGeneration = engine.getInitialPopulation(size);
		// else
		// currentGeneration = engine.getNextPopulation(size,
		// currentGeneration);
	}

}
