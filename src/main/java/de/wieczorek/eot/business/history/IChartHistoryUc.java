package de.wieczorek.eot.business.history;

import java.util.List;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;

public interface IChartHistoryUc {

	public ExchangeRateHistory getHistory(ExchangableType from, ExchangableType to, int hours);
}
