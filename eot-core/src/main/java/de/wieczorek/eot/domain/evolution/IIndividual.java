package de.wieczorek.eot.domain.evolution;

import java.util.List;

public interface IIndividual {

    double calculateFitness();

    void performAction();

    List<IIndividual> combineWith(IIndividual iIndividual);

    String getName();

    void mutate();
}
