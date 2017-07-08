package de.wieczorek.eot.domain.exchangable.rate;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
     * Key for the cache of the saved sub lists.
     *
     * @author Daniel Wieczorek
     *
     */
    private class ExchangeRateHistoryEntryKey {
	/**
	 * Last possible date of the entry.
	 */
	private final LocalDateTime start;

	/**
	 * Amount of entries before the start date.
	 */
	private final int amount;

	/**
	 * Constructor.
	 *
	 * @param startInput
	 *            last date
	 * @param amountInput
	 *            length of the history
	 */
	ExchangeRateHistoryEntryKey(final LocalDateTime startInput, final int amountInput) {
	    this.start = startInput;
	    this.amount = amountInput;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getOuterType().hashCode();
	    result = prime * result + amount;
	    result = prime * result + ((start == null) ? 0 : start.hashCode());
	    return result;
	}

	@Override
	public boolean equals(final Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    final ExchangeRateHistoryEntryKey other = (ExchangeRateHistoryEntryKey) obj;
	    if (!getOuterType().equals(other.getOuterType())) {
		return false;
	    }
	    if (amount != other.amount) {
		return false;
	    }
	    if (start == null) {
		if (other.start != null) {
		    return false;
		}
	    } else if (!start.equals(other.start)) {
		return false;
	    }
	    return true;
	}

	private ExchangeRateHistory getOuterType() {
	    return ExchangeRateHistory.this;
	}

    }

    /**
     * Default Constructor.
     */
    public ExchangeRateHistory() {
	dataPointsAsList = new ArrayList<>();
    }

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
	newHistory.dataPointsAsList.addAll(exchangeRates);
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
	return dataPointsAsList.get(dataPointsAsList.size() - 1);
    }

}
