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

}