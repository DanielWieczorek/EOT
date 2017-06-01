package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;

/**
 * Class represening a trade order issed by a trader.
 * 
 * @author Daniel Wieczorek
 *
 */
public class Order {

    /**
     * the pair to be traded between.
     */
    private ExchangablePair pair;

    /**
     * the amount of the exchangable to be traded.
     */
    private double amount;
    /**
     * Type of the order: Buy or sell.
     */
    private OrderType type;

    private double price;

    public final OrderType getType() {
	return type;
    }

    public final void setType(final OrderType typeInput) {
	this.type = typeInput;
    }

    /**
     * Constructor.
     * 
     * @param pairInput
     *            the currency pair to be traded
     * @param amountInput
     *            the amount to trade
     * @param typeInput
     *            the type of order buy/sell
     */
    public Order(final ExchangablePair pairInput, final double amountInput, final OrderType typeInput) {
	super();
	this.setPair(pairInput);
	this.setAmount(amountInput);
	this.type = typeInput;
    }

    /**
     * Default constructor.
     */
    public Order() {

    }

    public final ExchangablePair getPair() {
	return pair;
    }

    public final void setPair(final ExchangablePair pairInput) {
	this.pair = pairInput;
    }

    public final double getAmount() {
	return amount;
    }

    public final void setAmount(final double amountInput) {
	this.amount = amountInput;
    }

    public double getPrice() {
	return price;
    }

    public void setPrice(double price) {
	this.price = price;
    }

}
