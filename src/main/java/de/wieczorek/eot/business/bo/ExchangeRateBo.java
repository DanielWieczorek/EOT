package de.wieczorek.eot.business.bo;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "exchange_rate", schema = "eos@cassandra_pu")
public class ExchangeRateBo {
	@EmbeddedId
	private ExchangeRateBoKey key;

	@Column(name = "rate")
	private double exchangeRate;

	public ExchangeRateBo() {
	}

	public ExchangeRateBoKey getKey() {
		return key;
	}

	public void setKey(ExchangeRateBoKey key) {
		this.key = key;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

}