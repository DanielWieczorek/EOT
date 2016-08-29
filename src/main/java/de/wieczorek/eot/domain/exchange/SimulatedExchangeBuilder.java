package de.wieczorek.eot.domain.exchange;

import de.wieczorek.eot.business.history.impl.ChartHistoryUcImpl;
import de.wieczorek.eot.business.price.impl.ExchangeRateUcImpl;
import de.wieczorek.eot.dataaccess.CexAPI;
import de.wieczorek.eot.dataaccess.ExchangeRateDao;
import de.wieczorek.eot.dataaccess.Session;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;

public class SimulatedExchangeBuilder {

	public IExchange createExchange() {
		SimulatedExchangeImpl exchange = new SimulatedExchangeImpl(new ChartHistoryUcImpl(new ExchangeRateDao(new CexAPI(Session.getInstance()))),new ExchangeRateUcImpl(new ExchangeRateDao(new CexAPI(Session.getInstance()))));
		return exchange;
	}

}
