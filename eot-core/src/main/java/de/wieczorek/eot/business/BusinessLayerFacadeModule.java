package de.wieczorek.eot.business;

import com.google.inject.AbstractModule;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class BusinessLayerFacadeModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IBusinessLayerFacade.class).to(BusinessLayerFacadeImpl.class);
    }

}
