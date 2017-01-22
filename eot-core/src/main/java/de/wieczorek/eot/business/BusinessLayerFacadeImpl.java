package de.wieczorek.eot.business;

import javax.inject.Inject;

import de.wieczorek.eot.business.configuration.IConfigurationUc;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

/**
 * implementation of {@link IBusinessLayerFacade}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class BusinessLayerFacadeImpl implements IBusinessLayerFacade {

    /**
     * Used for access to chart data.
     */
    private IChartHistoryUc chartHistoryUc;

    /**
     * Used to retrieve the current exchange rate.
     */
    private IExchangeRateUc exchangeRateUc;

    /**
     * Used to read or alter the configuration.
     */
    private IConfigurationUc configurationUc;

    /**
     * Constructor.
     * 
     * @param chartHistoryUcInput
     *            the UC for accessing the chart history
     * @param exchangeRateUcInput
     *            the UC for reading the exchange rate
     * @param configurationUcInput
     *            the UC for reading and altering the configuration
     */
    @Inject
    public BusinessLayerFacadeImpl(final IChartHistoryUc chartHistoryUcInput, final IExchangeRateUc exchangeRateUcInput,
	    final IConfigurationUc configurationUcInput) {
	super();
	this.chartHistoryUc = chartHistoryUcInput;
	this.exchangeRateUc = exchangeRateUcInput;
	this.configurationUc = configurationUcInput;
    }

    @Override
    public final ExchangeRateHistory getHistory(final ExchangableType from, final ExchangableType to, final int hours) {
	return chartHistoryUc.getHistory(from, to, hours);
    }

    @Override
    public final ExchangeRateHistory getDetailedHistoryFromDb(final ExchangableType from, final ExchangableType to,
	    final int hours) {
	return chartHistoryUc.getDetailedHistoryFromDb(from, to, hours);
    }

    @Override
    public final TimedExchangeRate getCurrentExchangeRate(final ExchangableType from, final ExchangableType to) {
	return exchangeRateUc.getCurrentExchangeRate(from, to);
    }

    @Override
    public final double getOrderFees() {

	return configurationUc.getOrderFees();
    }

    @Override
    public final int getOrderExecutionTime() {

	return configurationUc.getOrderExecutionTime();
    }

}
