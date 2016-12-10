package de.wieczorek.eot.business;

import javax.inject.Inject;

import de.wieczorek.eot.business.configuration.IConfigurationUc;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

public class BusinessLayerFacadeImpl implements IBusinessLayerFacade {

    private IChartHistoryUc chartHistoryUc;
    private IExchangeRateUc exchangeRateUc;
    private IConfigurationUc configurationUc;

    @Inject
    public BusinessLayerFacadeImpl(IChartHistoryUc chartHistoryUc, IExchangeRateUc exchangeRateUc,
	    IConfigurationUc configurationUcInput) {
	super();
	this.chartHistoryUc = chartHistoryUc;
	this.exchangeRateUc = exchangeRateUc;
	this.configurationUc = configurationUcInput;
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

    @Override
    public double getOrderFees() {

	return configurationUc.getOrderFees();
    }

    @Override
    public int getOrderExecutionTime() {

	return configurationUc.getOrderExecutionTime();
    }

}
