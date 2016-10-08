package de.wieczorek.eot.domain.exchange;

/**
 * Class for building exchanges.
 *
 * @author Daniel Wieczorek
 *
 */
public class AbstractExchangeBuilder {

    /**
     * Creates an instance of a {@link SimulatedExchange}.
     *
     * @return an exchange
     */
    public final IExchange createSimulatedExchange() {

	return new SimulatedExchangeBuilder().createExchange();
    }

}
