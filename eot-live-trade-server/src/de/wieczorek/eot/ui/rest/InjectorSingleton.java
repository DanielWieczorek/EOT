package de.wieczorek.eot.ui.rest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.wieczorek.eot.business.BusinessLayerFacadeModule;
import de.wieczorek.eot.business.account.impl.AccountBalanceUcModule;
import de.wieczorek.eot.business.configuration.ConfigurationUcModule;
import de.wieczorek.eot.business.configuration.simulation.SimulationConfigurationUcModule;
import de.wieczorek.eot.business.history.impl.ChartHistoryUcModule;
import de.wieczorek.eot.business.price.impl.ExchangeRateUcModule;
import de.wieczorek.eot.business.trade.impl.TradeUcModule;
import de.wieczorek.eot.dataaccess.IExchangeApiModule;
import de.wieczorek.eot.domain.exchange.RealExchangeModule;
import de.wieczorek.eot.domain.exchange.order.SimulatedOrderBookModule;
import de.wieczorek.eot.domain.machine.RealMachineModule;
import de.wieczorek.eot.domain.trader.AccountModule;

public class InjectorSingleton {

    static final Injector injector = Guice.createInjector(new TradeUcModule(), new AccountBalanceUcModule(),
	    new AccountModule(), new IExchangeApiModule(), new ChartHistoryUcModule(), new RealExchangeModule(),
	    new RealMachineModule(), new SimulatedOrderBookModule(), new SimulationConfigurationUcModule(),
	    new ConfigurationUcModule(), new BusinessLayerFacadeModule(), new ExchangeRateUcModule());

    public static Injector getInjector() {
	return injector;
    }

}
