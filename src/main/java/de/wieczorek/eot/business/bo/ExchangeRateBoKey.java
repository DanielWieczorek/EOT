package de.wieczorek.eot.business.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import de.wieczorek.eot.domain.ExchangableType;

@Embeddable
public class ExchangeRateBoKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "from_currency")
	private ExchangableType fromCurrency;

	@Column(name = "timestamp")
	private long timestamp;

	@Column(name = "to_currency")
	private ExchangableType toCurrency;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ExchangableType getFromCurrency() {
		return fromCurrency;
	}

	public void setFromCurrency(ExchangableType fromCurrency) {
		this.fromCurrency = fromCurrency;
	}

	public ExchangableType getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(ExchangableType toCurrency) {
		this.toCurrency = toCurrency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromCurrency == null) ? 0 : fromCurrency.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((toCurrency == null) ? 0 : toCurrency.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExchangeRateBoKey other = (ExchangeRateBoKey) obj;
		if (fromCurrency == null) {
			if (other.fromCurrency != null)
				return false;
		} else if (!fromCurrency.equals(other.fromCurrency))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (toCurrency == null) {
			if (other.toCurrency != null)
				return false;
		} else if (!toCurrency.equals(other.toCurrency))
			return false;
		return true;
	}

	public ExchangeRateBoKey(long timestamp, ExchangableType fromCurrency, ExchangableType toCurrency) {
		super();
		this.timestamp = timestamp;
		this.fromCurrency = fromCurrency;
		this.toCurrency = toCurrency;
	}

	public ExchangeRateBoKey() {

	}

}
