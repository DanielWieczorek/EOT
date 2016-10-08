package de.wieczorek.eot.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Class representing a sequence of exchange rates.
 *
 * @author Daniel Wieczorek
 *
 */
public class ExchangeRateHistory {

    /**
     * A Queue containing the sequence of exchange rates. The data in this Queue
     * is the master. All other structures are just for caching and performance
     * optimization.
     */
    private final PriorityQueue<TimedExchangeRate> dataPoints;

    /**
     * contains the same data from the queue if initialized. This list is used
     * for Caching so that it does not have to generated with each call of
     * {@link getCompleteHistoryData()}}
     */
    private List<TimedExchangeRate> dataPointsAsList;

    /**
     * contains a subset of the queue which was generated via
     * getHistoryEntriesBefore(). This is used for caching when multiple traders
     * retrieve the same history subset. The key is the start date and the
     * amount of entries.
     */
    private final Map<ExchangeRateHistoryEntryKey, List<TimedExchangeRate>> entriesBeforeBuffer;

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
	dataPoints = new PriorityQueue<>();
	dataPointsAsList = new LinkedList<>();
	entriesBeforeBuffer = new HashMap<>();
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

	for (final TimedExchangeRate exchangeRate : exchangeRates) {
	    newHistory.add(exchangeRate);
	}
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
	dataPoints.add(exchangeRate);
	dataPointsAsList = null;
	entriesBeforeBuffer.clear();
	return this;
    }

    /**
     * Returns the complete history as list of exchange rates. Uses the internal
     * cache if possible.
     *
     * @return a list of exchange rates.
     */
    public final List<TimedExchangeRate> getCompleteHistoryData() {
	if (dataPointsAsList == null) {
	    dataPointsAsList = dataPoints.stream().collect(Collectors.toList());
	}
	return dataPointsAsList;

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
    public final ExchangeRateHistory getHistoryEntriesBefore(final LocalDateTime date, final int amount) {

	List<TimedExchangeRate> resultExchangeRates = entriesBeforeBuffer
		.get(new ExchangeRateHistoryEntryKey(date.withSecond(0).withNano(0), amount));
	if (resultExchangeRates == null) {
	    resultExchangeRates = dataPointsAsList.parallelStream().filter(b -> b.getTime().isBefore(date)).sorted()
		    .collect(Collectors.toList());

	    entriesBeforeBuffer.putIfAbsent(new ExchangeRateHistoryEntryKey(date, amount), resultExchangeRates);
	}

	if (!resultExchangeRates.isEmpty()) {
	    return ExchangeRateHistory.from(resultExchangeRates
		    .subList(Math.max(0, resultExchangeRates.size() - 1 - amount), resultExchangeRates.size() - 1));
	} else {
	    return new ExchangeRateHistory();
	}

    }
}
