package de.wieczorek.eot.domain.exchangable;

/**
 * Class representing a pair of exchangeable goods e.g. currencies.
 *
 * @author Daniel Wieczorek
 *
 */
public class ExchangablePair {

    /**
     * source exchangeable.
     */
    private ExchangableType from;
    /**
     * source exchangeable.
     */
    private ExchangableType to;

    public final ExchangableType getFrom() {
	return from;
    }

    public final void setFrom(final ExchangableType fromInput) {
	this.from = fromInput;
    }

    public final ExchangableType getTo() {
	return to;
    }

    public final void setTo(final ExchangableType toInput) {
	this.to = toInput;
    }

    public ExchangablePair() {

    }

    /**
     * Constructor.
     *
     * @param fromInput
     *            source exchangeable
     * @param toInput
     *            target exchangeable
     */
    public ExchangablePair(final ExchangableType fromInput, final ExchangableType toInput) {
	super();
	this.from = fromInput;
	this.to = toInput;
    }
}
