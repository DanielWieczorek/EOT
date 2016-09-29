package de.wieczorek.eot.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ExchangeRateHistory {

	private PriorityQueue<TimedExchangeRate> dataPoints;
	private List<TimedExchangeRate> dataPointsAsList;
	private Map<ExchangeRateHistoryEntryKey, List<TimedExchangeRate>> entriesBeforeBuffer;

	private class ExchangeRateHistoryEntryKey {
		private LocalDateTime start;
		private int amount;

		public ExchangeRateHistoryEntryKey(LocalDateTime start, int amount) {
			this.start = start;
			this.amount = amount;
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
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ExchangeRateHistoryEntryKey other = (ExchangeRateHistoryEntryKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (amount != other.amount)
				return false;
			if (start == null) {
				if (other.start != null)
					return false;
			} else if (!start.equals(other.start))
				return false;
			return true;
		}

		private ExchangeRateHistory getOuterType() {
			return ExchangeRateHistory.this;
		}

	}

	public ExchangeRateHistory() {
		dataPoints = new PriorityQueue<>();
		dataPointsAsList = new LinkedList<>();
		entriesBeforeBuffer = new HashMap<>();
	}

	public static ExchangeRateHistory from(List<TimedExchangeRate> exchangeRates) {
		ExchangeRateHistory newHistory = new ExchangeRateHistory();

		for (TimedExchangeRate exchangeRate : exchangeRates)
			newHistory.add(exchangeRate);
		return newHistory;

	}

	public ExchangeRateHistory add(TimedExchangeRate exchangeRate) {
		dataPoints.add(exchangeRate);
		dataPointsAsList = null;
		return this;
	}

	public List<TimedExchangeRate> getCompleteHistoryData() {
		if (dataPointsAsList == null)
			dataPointsAsList = dataPoints.stream().collect(Collectors.toList());
		return dataPointsAsList;

	}

	public ExchangeRateHistory getHistoryEntriesBefore(LocalDateTime date, int amount) {

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
		} else
			return new ExchangeRateHistory();

	}
}
