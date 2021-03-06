package de.wieczorek.eot.business;

import java.util.List;

import javax.inject.Inject;

import de.wieczorek.eot.business.account.IAccountBalanceUc;
import de.wieczorek.eot.business.configuration.IConfigurationUc;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.business.trade.ITradeUc;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.Order;

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
     * Used to read information about the current account balance.
     */
    private IAccountBalanceUc accountBalanceUc;

    /**
     * UC to perform trades.
     */
    private ITradeUc tradeUc;

    /**
     * Constructor.
     * 
     * @param chartHistoryUcInput
     *            the UC for accessing the chart history
     * @param exchangeRateUcInput
     *            the UC for reading the exchange rate
     * @param configurationUcInput
     *            the UC for reading and altering the configuration
     * @param accountBalanceUc
     *            the UC to retrieve the account balance
     * @param tradeUc
     *            the UC for performing trades
     * 
     */
    @Inject
    public BusinessLayerFacadeImpl(final IChartHistoryUc chartHistoryUcInput, final IExchangeRateUc exchangeRateUcInput,
	    final IConfigurationUc configurationUcInput, final IAccountBalanceUc accountBalanceUc,
	    final ITradeUc tradeUc) {
	super();
	this.chartHistoryUc = chartHistoryUcInput;
	this.exchangeRateUc = exchangeRateUcInput;
	this.configurationUc = configurationUcInput;
	this.accountBalanceUc = accountBalanceUc;
	this.tradeUc = tradeUc;
    }

    @Override
    public final ExchangeRateHistory getDetailedHistoryFromDb(final ExchangableType from, final ExchangableType to,
	    final int minutes) {
	return chartHistoryUc.getDetailedHistoryFromDb(from, to, minutes);
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

    @Override
    public final List<ExchangableSet> getAccountBalance() {

	return accountBalanceUc.getAccountBalance();
    }

    @Override
    public final void perform(final Order order) {
	tradeUc.perform(order);
    }

    @Override
    public final String getKey() {

	return configurationUc.getKey();
    }

    @Override
    public final String getSecret() {
	return configurationUc.getSecret();
    }

}
