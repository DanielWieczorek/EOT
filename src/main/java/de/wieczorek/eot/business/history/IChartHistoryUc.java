package de.wieczorek.eot.business.history;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;

public interface IChartHistoryUc {

	public ExchangeRateHistory getHistory(ExchangableType from, ExchangableType to, int hours);

	public ExchangeRateHistory getDetailedHistory(ExchangableType from, ExchangableType to, int hours);
}
