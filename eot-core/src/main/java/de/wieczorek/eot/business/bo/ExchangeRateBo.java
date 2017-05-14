package de.wieczorek.eot.business.bo;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Class for the ORM mapping of exchange rates.
 * 
 * @author Daniel Wieczorek
 *
 */
@Entity
@Table(name = "exchange_rate", schema = "eos@cassandra_pu")
public class ExchangeRateBo {

    /**
     * The key containing a timestamp and the two currencies.
     */
    @EmbeddedId
    private ExchangeRateBoKey key;

    /**
     * The exchange rate.
     */
    @Column(name = "rate")
    private double exchangeRate;

    /**
     * Default constructor.
     */
    public ExchangeRateBo() {
    }

    public final ExchangeRateBoKey getKey() {
	return key;
    }

    public final void setKey(final ExchangeRateBoKey keyToSet) {
	this.key = keyToSet;
    }

    public final double getExchangeRate() {
	return exchangeRate;
    }

    public final void setExchangeRate(final double exchangeRateToSet) {
	this.exchangeRate = exchangeRateToSet;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	long temp;
	temp = Double.doubleToLongBits(exchangeRate);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	result = prime * result + ((key == null) ? 0 : key.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	ExchangeRateBo other = (ExchangeRateBo) obj;
	if (Double.doubleToLongBits(exchangeRate) != Double.doubleToLongBits(other.exchangeRate)) {
	    return false;
	}
	if (key == null) {
	    if (other.key != null) {
		return false;
	    }
	} else if (!key.equals(other.key)) {
	    return false;
	}
	return true;
    }

}