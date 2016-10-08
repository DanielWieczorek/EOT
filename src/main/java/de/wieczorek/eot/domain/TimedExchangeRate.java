package de.wieczorek.eot.domain;

import java.time.LocalDateTime;

/**
 * Class representing an exchange rate between two currencies at a given point
 * in time.
 *
 * @author Daniel Wieczorek
 *
 */
public class TimedExchangeRate implements Comparable<TimedExchangeRate> {

    /**
     * source currency. TODO use exchangable Pair
     */
    private ExchangableType from;

    /**
     * Target currency.
     */
    private ExchangableType to;

    /**
     * the exchange rate. <amount of target currency> = <amount of source
     * currency> *toPrice
     */
    private double toPrice;

    /**
     * Time at which this exchange rate applies.
     */
    private LocalDateTime time;

    /**
     * Constructor.
     *
     * @param fromInput
     *            source currency
     * @param toInput
     *            target currency
     * @param toPriceInput
     *            exchange rate
     * @param timeInput
     *            time at which the exchange rate applies
     */
    public TimedExchangeRate(final ExchangableType fromInput, final ExchangableType toInput, final double toPriceInput,
	    final LocalDateTime timeInput) {
	super();
	this.from = fromInput;
	this.to = toInput;
	this.toPrice = toPriceInput;
	this.time = timeInput;
    }

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

    public final double getToPrice() {
	return toPrice;
    }

    public final void setToPrice(final double toPriceInput) {
	this.toPrice = toPriceInput;
    }

    public final LocalDateTime getTime() {
	return time;
    }

    public final void setTime(final LocalDateTime timeInput) {
	this.time = timeInput;
    }

    /**
     * Checks whether the given exchange rates date time is after the date time
     * of this exchange rate.
     *
     * @param exchangeRate
     *            exchange to compare with.
     * @return true if this exchange rate is before the given exchange rate
     */
    public final boolean isBefore(final TimedExchangeRate exchangeRate) {
	return this.time.isBefore(exchangeRate.time);
    }

    /**
     * Swaps the source and the target currency and updates the exchange rate
     * accordingly.
     *
     * @return a new instance of this exchange rate with swapped currencies.
     */
    public final TimedExchangeRate swap() {
	return new TimedExchangeRate(to, from, 1 / toPrice, time);
    }

    @Override
    public final int compareTo(final TimedExchangeRate arg0) {
	if (arg0 instanceof TimedExchangeRate) {
	    if (arg0.getTime().isBefore(this.time)) {
		return 1;
	    } else if (arg0.getTime().isAfter(this.time)) {
		return -1;
	    } else {
		return 0;
	    }
	}
	return -1;
    }

}
