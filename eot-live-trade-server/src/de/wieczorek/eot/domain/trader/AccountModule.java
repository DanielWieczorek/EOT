package de.wieczorek.eot.domain.trader;

import com.google.inject.AbstractModule;

public class AccountModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IAccount.class).to(SynchronizingAccount.class);
    }

}
