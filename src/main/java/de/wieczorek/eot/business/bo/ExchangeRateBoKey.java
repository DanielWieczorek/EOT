package de.wieczorek.eot.business.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import de.wieczorek.eot.domain.exchangable.ExchangableType;

/**
 * Key for the ExchangeRateBo.
 *
 * @author Daniel Wieczorek
 *
 */
@Embeddable
public class ExchangeRateBoKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * source currency for the index. E.g. the ETH for the index ETH/BTC
     */
    @Column(name = "from_currency")
    private ExchangableType fromCurrency;

    /**
     * Time stamp for the entry.
     */
    @Column(name = "timestamp")
    private long timestamp;

    /**
     * target currency for the index. E.g. the ETH for the index ETH/BTC
     */
    @Column(name = "to_currency")
    private ExchangableType toCurrency;

    public final long getTimestamp() {
	return timestamp;
    }

    public final void setTimestamp(final long timestampToSet) {
	this.timestamp = timestampToSet;
    }

    public final ExchangableType getFromCurrency() {
	return fromCurrency;
    }

    public final void setFromCurrency(final ExchangableType fromCurrencyToSet) {
	this.fromCurrency = fromCurrencyToSet;
    }

    public final ExchangableType getToCurrency() {
	return toCurrency;
    }

    public final void setToCurrency(final ExchangableType toCurrencyToSet) {
	this.toCurrency = toCurrencyToSet;
    }

    @Override
    public final int hashCode() {
	final int prime = 31;
	final int i = 32;
	int result = 1;
	result = prime * result + ((fromCurrency == null) ? 0 : fromCurrency.hashCode());
	result = prime * result + (int) (timestamp ^ (timestamp >>> i));
	result = prime * result + ((toCurrency == null) ? 0 : toCurrency.hashCode());
	return result;
    }

    @Override
    public final boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ExchangeRateBoKey other = (ExchangeRateBoKey) obj;
	if (fromCurrency == null) {
	    if (other.fromCurrency != null) {
		return false;
	    }
	} else if (!fromCurrency.equals(other.fromCurrency)) {
	    return false;
	}
	if (timestamp != other.timestamp) {
	    return false;
	}
	if (toCurrency == null) {
	    if (other.toCurrency != null) {
		return false;
	    }
	} else if (!toCurrency.equals(other.toCurrency)) {
	    return false;
	}
	return true;
    }

    /**
     * Constructor.
     *
     * @param timestampToSet
     *            the time stamp
     * @param fromCurrencyToSet
     *            source currency
     * @param toCurrencyToSet
     *            target currency
     */
    public ExchangeRateBoKey(final long timestampToSet, final ExchangableType fromCurrencyToSet,
	    final ExchangableType toCurrencyToSet) {
	super();
	this.timestamp = timestampToSet;
	this.fromCurrency = fromCurrencyToSet;
	this.toCurrency = toCurrencyToSet;
    }

    /**
     * Default constructor.
     */
    public ExchangeRateBoKey() {

    }

}
