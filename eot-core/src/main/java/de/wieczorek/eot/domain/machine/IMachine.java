package de.wieczorek.eot.domain.machine;

public interface IMachine {

    /**
     * Starts the machine.
     */
    public abstract void start();

    /**
     * Pauses the machine, so that it can resume at the same location it was
     * stopped.
     */
    public abstract void pause();

    /**
     * Stops the machine. If started again the machine wil start from the
     * beginning.
     */
    public abstract void stop();
}
