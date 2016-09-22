package de.wieczorek.eot.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ExchangeRateHistory {

	private PriorityQueue<TimedExchangeRate> dataPoints;

	public ExchangeRateHistory() {
		dataPoints = new PriorityQueue<>();
	}

	public static ExchangeRateHistory from(List<TimedExchangeRate> exchangeRates) {
		ExchangeRateHistory newHistory = new ExchangeRateHistory();

		for (TimedExchangeRate exchangeRate : exchangeRates)
			newHistory.add(exchangeRate);
		return newHistory;

	}

	public ExchangeRateHistory add(TimedExchangeRate exchangeRate) {
		dataPoints.add(exchangeRate);

		return this;
	}

	public List<TimedExchangeRate> getCompleteHistoryData() {

		return dataPoints.stream().collect(Collectors.toList());

	}

	public ExchangeRateHistory getHistoryEntriesBefore(LocalDateTime date, int amount) {
		List<TimedExchangeRate> resultExchangeRates = dataPoints.stream().filter(b -> b.getTime().isBefore(date))
				.collect(Collectors.toList());

		if (!resultExchangeRates.isEmpty()) {
			return ExchangeRateHistory.from(resultExchangeRates
					.subList(Math.max(0, resultExchangeRates.size() - 1 - amount), resultExchangeRates.size() - 1));
		} else
			return new ExchangeRateHistory();

	}
}
