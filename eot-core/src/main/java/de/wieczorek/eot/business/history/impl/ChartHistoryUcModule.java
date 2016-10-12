package de.wieczorek.eot.business.history.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.dataaccess.Session;

public class ChartHistoryUcModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IChartHistoryUc.class).to(ChartHistoryUcImpl.class);

    }

    @Provides
    Session provideSession() {
	return Session.getInstance();
    }
}
