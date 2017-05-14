package de.wieczorek.eot.business.history;

import java.io.IOException;

import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

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
     * Retrieves the detailed exchange rate history from the database.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param hours
     *            number of hours for which to retrieve the currency
     * @return the exchange rate history containing all data points.
     * @throws IOException
     */
    ExchangeRateHistory getDetailedHistoryFromDb(ExchangableType from, ExchangableType to, int hours);
}
