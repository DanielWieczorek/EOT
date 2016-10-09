package de.wieczorek.eot.business.price;

import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

/**
 * Class for determining the current exchange rate for a pair of currencies.
 *
 * @author Daniel Wieczorek
 *
 */
public interface IExchangeRateUc {
    /**
     * Returns the current exchange rate for the two given currencies.
     *
     * @param from
     *            source exchangable
     * @param to
     *            target exchangable
     * @return the exchange rate for the current time.
     */
    TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to);
}
