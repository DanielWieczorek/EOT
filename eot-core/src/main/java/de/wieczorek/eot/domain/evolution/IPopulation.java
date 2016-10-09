package de.wieczorek.eot.domain.evolution;

import java.util.List;

import de.wieczorek.eot.domain.trader.Trader;

public interface IPopulation {

	public void add(Trader trader);

	public List<Trader> getAll();

	public void getNextPopulation(int size);
}
