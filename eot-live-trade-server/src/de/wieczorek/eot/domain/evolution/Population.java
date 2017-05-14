package de.wieczorek.eot.domain.evolution;

import java.util.LinkedList;
import java.util.List;

public class Population implements IPopulation {

    private List<IIndividual> currentGeneration;

    public Population() {
	currentGeneration = new LinkedList<>();
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
    public void clearPopulation() {
	currentGeneration.clear();
    }

    @Override
    public void getNextPopulation(int size) {

    }

    @Override
    public int getPopulationNumber() {
	return 0;
    }

}
