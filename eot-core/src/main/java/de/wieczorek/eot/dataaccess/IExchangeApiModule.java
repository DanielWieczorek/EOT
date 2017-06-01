package de.wieczorek.eot.dataaccess;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.dataaccess.kraken.KrakenApiWrapper;

public class IExchangeApiModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IExchangeApi.class).to(KrakenApiWrapper.class);

    }

}
