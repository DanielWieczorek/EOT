package de.wieczorek.eot.business.history.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.dataaccess.Session;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ChartHistoryUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IChartHistoryUc.class).to(ChartHistoryUcImpl.class);

    }

    /**
     * Provides a session for the access to the API of the exchange.
     * 
     * @return session
     */
    @Provides
    final Session provideSession() {
	return Session.getInstance();
    }
}
