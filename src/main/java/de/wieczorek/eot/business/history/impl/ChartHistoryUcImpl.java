package de.wieczorek.eot.business.history.impl;

import org.json.JSONException;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.dataaccess.ExchangeRateDao;
import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.ExchangeRateHistory;

public class ChartHistoryUcImpl implements IChartHistoryUc{

	private ExchangeRateDao dao;
	@Override
	public ExchangeRateHistory getHistory(ExchangableType from, ExchangableType to, int hours) {
	
		try {
			return ExchangeRateHistory.from(dao.getHistoryEntries(from, to, hours, 1000));
		} catch (JSONException e) {
			e.printStackTrace();
		};
		return new ExchangeRateHistory();
	}

	public ChartHistoryUcImpl(ExchangeRateDao dao){
		this.dao = dao;
	}
}
