package de.wieczorek.eot.business;

import javax.inject.Inject;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class BusinessLayerFacade implements IChartHistoryUc, IExchangeRateUc {

    private IChartHistoryUc chartHistoryUc;
    private IExchangeRateUc exchangeRateUc;

    @Inject
    public BusinessLayerFacade(IChartHistoryUc chartHistoryUc, IExchangeRateUc exchangeRateUc) {
	super();
	this.chartHistoryUc = chartHistoryUc;
	this.exchangeRateUc = exchangeRateUc;
    }

    @Override
    public ExchangeRateHistory getHistory(ExchangableType from, ExchangableType to, int hours) {
	return chartHistoryUc.getHistory(from, to, hours);
    }

    @Override
    public ExchangeRateHistory getDetailedHistoryFromDb(ExchangableType from, ExchangableType to, int hours) {
	return chartHistoryUc.getDetailedHistoryFromDb(from, to, hours);
    }

    @Override
    public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to) {
	return exchangeRateUc.getCurrentExchangeRate(from, to);
    }

}
