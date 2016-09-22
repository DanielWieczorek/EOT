package de.wieczorek.eot.business.history.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import de.wieczorek.eot.business.bo.ExchangeRateBo;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.dataaccess.ExchangeRateDao;
import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;

public class ChartHistoryUcImpl implements IChartHistoryUc {

	private ExchangeRateDao dao;

	@Override
	public ExchangeRateHistory getHistory(ExchangableType from, ExchangableType to, int hours) {

		try {
			return ExchangeRateHistory.from(dao.getHistoryEntries(from, to, hours, 1000));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		;
		return new ExchangeRateHistory();
	}

	public ChartHistoryUcImpl(ExchangeRateDao dao) {
		this.dao = dao;
	}

	@Override
	public ExchangeRateHistory getDetailedHistory(ExchangableType from, ExchangableType to, int hours) {
		ExchangeRateHistory result = null;
		List<ExchangeRateBo> bos;
		// try {
		// bos = dao.getDetailedHistoryEntries(from, to, hours);
		//
		// dao.saveHistoryEntries(bos);
		List<TimedExchangeRate> ter = new ArrayList<>();
		for (ExchangeRateBo item : dao.getDetailedHistoryEntriesFromDb(from, to, hours))
			ter.add(new TimedExchangeRate(item.getKey().getFromCurrency(), item.getKey().getToCurrency(),
					item.getExchangeRate(),
					LocalDateTime.ofInstant(Instant.ofEpochSecond(item.getKey().getTimestamp()), ZoneId.of("GMT"))));
		result = ExchangeRateHistory.from(ter);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return result;
	}
}
