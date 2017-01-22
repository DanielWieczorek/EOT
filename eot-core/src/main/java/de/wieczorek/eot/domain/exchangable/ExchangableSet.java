package de.wieczorek.eot.domain.exchangable;

/**
 * Class representing a given amount of exchangables.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ExchangableSet {

    /**
     * The type of exchangable. E.g. the currency.
     */
    private ExchangableType exchangable;
    /**
     * the amount of the exchangable.
     */
    private double amount;

    /**
     * Merges another {@link ExchangableSet} with this object. The amounts are
     * added in this cases.
     * 
     * @param mergePartner
     *            the exchangable to merge this {@link ExchangableSet} with.
     * @return a new instance of {@link ExchangableSet} containing the merge
     *         result
     */
    public final ExchangableSet mergeWith(final ExchangableSet mergePartner) {
	// TODO exception if types are different
	return new ExchangableSet(exchangable, amount + mergePartner.amount);
    }

    /**
     * Default constructor.
     */
    public ExchangableSet() {

    }

    /**
     * Constructor.
     * 
     * @param exchangableInput
     *            the exchangable
     * @param amountInput
     *            the amount of this exchangable
     */
    public ExchangableSet(final ExchangableType exchangableInput, final double amountInput) {
	this.exchangable = exchangableInput;
	this.amount = amountInput;
    }

    public final ExchangableType getExchangable() {
	return exchangable;
    }

    public final void setExchangable(final ExchangableType exchangableInput) {
	this.exchangable = exchangableInput;
    }

    public final double getAmount() {
	return amount;
    }

    public final void setAmount(final double amountInput) {
	this.amount = amountInput;
    }
}
