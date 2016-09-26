package de.wieczorek.eot.domain;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ExchangeRateHistory {

	private PriorityQueue<TimedExchangeRate> dataPoints;
	private List<TimedExchangeRate> dataPointsAsList;

	public ExchangeRateHistory() {
		dataPoints = new PriorityQueue<>();
		dataPointsAsList = new LinkedList<>();
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
		List<TimedExchangeRate> resultExchangeRates = dataPoints.parallelStream()
				.filter(b -> b.getTime().isBefore(date)).sorted().collect(Collectors.toList());

		if (!resultExchangeRates.isEmpty()) {
			return ExchangeRateHistory.from(resultExchangeRates
					.subList(Math.max(0, resultExchangeRates.size() - 1 - amount), resultExchangeRates.size() - 1));
		} else
			return new ExchangeRateHistory();

	}
}
