package de.wieczorek.eot.business.history;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;

/**
 * Use case class to retrieve the exchange rate history from the exchange API.
 * It also converts the data from the DAO to the data, which the domain layer
 * understands.
 *
 * @author Daniel Wieczorek
 *
 */
public interface IChartHistoryUc {
    /**
     * Retrieves the history from the exchange using the official functionality.
     * This is approximately precise for 15 minutes.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param hours
     *            number of hours for which to retrieve the currency
     * @return the exchange rate history containing all data points.
     */
    ExchangeRateHistory getHistory(ExchangableType from, ExchangableType to, int hours);

    /**
     * Retrieves the detailed exchange rate history from the database.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param hours
     *            number of hours for which to retrieve the currency
     * @return the exchange rate history containing all data points.
     */
    ExchangeRateHistory getDetailedHistoryFromDb(ExchangableType from, ExchangableType to, int hours);
}
