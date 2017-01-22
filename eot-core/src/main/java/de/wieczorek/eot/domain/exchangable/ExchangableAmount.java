package de.wieczorek.eot.domain.exchangable;

/**
 * Class representing a certain amount of an Exchangables. Additionally to the
 * {@link ExchangableSet} this class also contains the price at which this
 * exchangable was acquired.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ExchangableAmount {

    /**
     * Price in Euro at which the exchangables were acquired.
     */
    private double aquiredAtPrice; // TODO implementation so that this is the
				   // BTC equivalent
    /**
     * Class representing the exchangables and their amount.
     */
    private ExchangableSet exchangableAmount;

    /**
     * Constructor.
     * 
     * @param exchangableAmountInput
     *            the exchangable and the amount
     * @param aquiredAtPriceInput
     *            price in euro at which the given amount of exchangables were
     *            acquired
     */
    public ExchangableAmount(final ExchangableSet exchangableAmountInput, final double aquiredAtPriceInput) {
	this.exchangableAmount = exchangableAmountInput;
	this.aquiredAtPrice = aquiredAtPriceInput;
    }

    /**
     * Default Constructor.
     */
    public ExchangableAmount() {

    }

    /**
     * @return the price at which this amount of exchangables were acquired.
     */
    public final double getAquiredAtPrice() {
	return aquiredAtPrice;
    }

    public final void setAquiredAtPrice(final double aquiredAtPriceInput) {
	this.aquiredAtPrice = aquiredAtPriceInput;
    }

    public final ExchangableSet getExchangableAmount() {
	return exchangableAmount;
    }

    public final void setExchangableAmount(final ExchangableSet exchangableAmountInput) {
	this.exchangableAmount = exchangableAmountInput;
    }
}
