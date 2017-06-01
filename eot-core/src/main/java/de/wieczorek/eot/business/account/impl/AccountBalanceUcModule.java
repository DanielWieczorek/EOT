package de.wieczorek.eot.business.account.impl;

import com.google.inject.AbstractModule;

import de.wieczorek.eot.business.account.IAccountBalanceUc;

/**
 * Module for dependency injection.
 * 
 * @author Daniel Wieczorek
 *
 */
public class AccountBalanceUcModule extends AbstractModule {

    @Override
    protected final void configure() {
	bind(IAccountBalanceUc.class).to(AccountBalanceUcImpl.class);
    }

}
