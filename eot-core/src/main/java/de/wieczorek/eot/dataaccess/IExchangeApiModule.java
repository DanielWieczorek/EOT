package de.wieczorek.eot.dataaccess;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.dataaccess.kraken.KrakenApiWrapper;

/**
 * Module for the {@link IExchangeApi}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class IExchangeApiModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IExchangeApi.class).to(KrakenApiWrapper.class);

    }

}
