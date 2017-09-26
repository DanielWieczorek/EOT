package de.wieczorek.eot.domain.exchangable.rate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a sequence of exchange rates.
 *
 * @author Daniel Wieczorek
 *
 */
public class ExchangeRateHistory {

    /**
     * contains the same data from the queue if initialized. This list is used
     * for Caching so that it does not have to generated with each call of
     * {@link getCompleteHistoryData()}}
     */
    private List<TimedExchangeRate> dataPointsAsList;

    /**
     * Default Constructor.
     */
    public ExchangeRateHistory() {
	dataPointsAsList = new ArrayList<>();
    }

    private TimedExchangeRate mostRecent;

    /**
     * Creates a new exchange rate history object from the given list of
     * exchange rates.
     *
     * @param exchangeRates
     *            list of exchange rates containing the history.
     * @return the new exchange rate history object
     */
    public static ExchangeRateHistory from(final List<TimedExchangeRate> exchangeRates) {
	final ExchangeRateHistory newHistory = new ExchangeRateHistory();

	exchangeRates.forEach(e -> newHistory.add(e));
	return newHistory;

    }

    public static ExchangeRateHistory fromSortedList(final List<TimedExchangeRate> exchangeRates) {
	final ExchangeRateHistory newHistory = new ExchangeRateHistory();
	newHistory.dataPointsAsList = Collections.unmodifiableList(exchangeRates);
	return newHistory;

    }

    /**
     * Adds a new time to this history. The entry is automatically added to the
     * correct position in the internal queue. This also invalidates all caches
     *
     * @param exchangeRate
     *            the exchange rate object to add
     * @return this exchange rate history containing the new exchange rate
     *         entry.
     */
    public final ExchangeRateHistory add(final TimedExchangeRate exchangeRate) {
	int index = getIndexByDate(exchangeRate.getTime());
	dataPointsAsList.add(index, exchangeRate);

	return this;
    }

    /**
     * Returns the complete history as list of exchange rates. Uses the internal
     * cache if possible.
     *
     * @return a list of exchange rates.
     */
    public final List<TimedExchangeRate> getCompleteHistoryData() {
	return dataPointsAsList;

    }

    private int getIndexByDate(LocalDateTime date) {
	int low = 0;
	int high = dataPointsAsList.size() - 1;
	int result = 0;
	int middle = 0;
	while (high >= low) {
	    middle = (low + high) >>> 1;
	    if (dataPointsAsList.get(middle).getTime().isBefore(date)) {
		low = middle + 1;
		result = low;
	    } else if (dataPointsAsList.get(middle).getTime().isAfter(date)) {
		high = middle - 1;
		result = high;
	    } else {
		result = middle;
		break;
	    }
	}
	return result;
    }

    /**
     * Returns all history entries before the given date. The given amount
     * specifies the maximum of entries returned. The list of resulting exchange
     * rates are cached in an internal map.
     *
     * @param date
     *            reference date time
     * @param amount
     *            maximum amount of entries
     * @return an exchange rate history entry containing a subset of the
     *         history.
     */

    long timeInMillis = 0;
    long counter = 0;

    public final ExchangeRateHistory getHistoryEntriesBefore(final LocalDateTime date, final int amount) {

	List<TimedExchangeRate> resultExchangeRates = null;

	int low = 0;
	int high = dataPointsAsList.size();
	while (high >= low) {
	    int middle = (low + high) / 2;
	    if (dataPointsAsList.get(middle).getTime().isBefore(date)) {
		low = middle + 1;
	    } else if (dataPointsAsList.get(middle).getTime().isAfter(date)) {
		high = middle - 1;
	    } else {
		resultExchangeRates = dataPointsAsList.subList(Math.max(0, middle - amount), middle);
		break;
	    }
	}

	if (!resultExchangeRates.isEmpty()) {
	    ExchangeRateHistory result = ExchangeRateHistory.fromSortedList(resultExchangeRates);
	    return result;
	} else {
	    return new ExchangeRateHistory();
	}

    }

    /**
     * Returns the most recent exchange rate. This is the last entry in the
     * internal queue/list.
     * 
     * @return a {@link TimedExchangeRate} representing exchange rate.
     */
    public final TimedExchangeRate getMostRecentExchangeRate() {
	if (mostRecent == null) {
	    mostRecent = dataPointsAsList.get(dataPointsAsList.size() - 1);
	}
	return mostRecent;
    }

    public ExchangeRateHistory getEntriesForMinutes(int numberOfMinutes) {
	int endIndex = numberOfMinutes;
	if (endIndex > dataPointsAsList.size() - 1) {
	    endIndex = dataPointsAsList.size() - 1;
	}
	return ExchangeRateHistory.fromSortedList(dataPointsAsList.subList(0, endIndex));
    }

}
