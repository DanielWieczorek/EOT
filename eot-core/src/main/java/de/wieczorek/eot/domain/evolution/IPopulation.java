package de.wieczorek.eot.domain.evolution;

import java.util.List;

public interface IPopulation {

    public void add(IIndividual trader);

    public List<IIndividual> getAll();

    public void getNextPopulation(int size);
}
