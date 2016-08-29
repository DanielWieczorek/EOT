package de.wieczorek.eot.business.price.impl;

import org.json.JSONException;

import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.dataaccess.ExchangeRateDao;
import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.TimedExchangeRate;

public class ExchangeRateUcImpl implements IExchangeRateUc {

	private ExchangeRateDao dao;
	
	public ExchangeRateUcImpl(ExchangeRateDao dao){
		this.dao = dao;
	}
	
	@Override
	public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to) {
		
		try {
			return dao.getCurrentExchangeRate(from, to);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new TimedExchangeRate(from, to, 0.0, null);
	}

}
