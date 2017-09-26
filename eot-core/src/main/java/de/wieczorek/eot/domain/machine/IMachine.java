package de.wieczorek.eot.domain.machine;

import de.wieczorek.eot.domain.evolution.IIndividual;

public interface IMachine {

    /**
     * Starts the machine.
     */
    void start();

    /**
     * Pauses the machine, so that it can resume at the same location it was
     * stopped.
     */
    void pause();

    /**
     * Stops the machine. If started again the machine wil start from the
     * beginning.
     */
    void stop();

    IIndividual getTraderById(long id);
}
