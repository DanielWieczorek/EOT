package de.wieczorek.eot.domain.machine;

/**
 * Represents the current state of a machine.
 * 
 * @author Daniel Wieczorek
 *
 */
public enum MachineState {
    /**
     * Machine was started and is currently running.
     */
    STARTED,
    /**
     * Machine is paused and will continue at the same location it was paused.
     */
    PAUSED,
    /**
     * The machine is stopped and will start from the beginning if started.
     */
    STOPPED
}
