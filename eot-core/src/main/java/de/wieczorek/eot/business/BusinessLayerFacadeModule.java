package de.wieczorek.eot.business;

import com.google.inject.AbstractModule;

public class BusinessLayerFacadeModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IBusinessLayerFacade.class).to(BusinessLayerFacadeImpl.class);
    }

}
