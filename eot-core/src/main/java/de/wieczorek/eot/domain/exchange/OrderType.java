package de.wieczorek.eot.domain.exchange;

/**
 * The type of order.
 * 
 * @author Daniel Wieczorek
 *
 */
public enum OrderType {

    /**
     * Buys an amount of the quote currency with a given amount of the base
     * currency.
     */
    BUY,
    /**
     * Sells a given amount of the quote currency for an amount of the base
     * currency.
     */
    SELL;
}
