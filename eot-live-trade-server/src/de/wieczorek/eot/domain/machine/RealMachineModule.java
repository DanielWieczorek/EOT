package de.wieczorek.eot.domain.machine;

import com.google.inject.AbstractModule;

public class RealMachineModule extends AbstractModule {

    @Override
    protected void configure() {
	bind(IMachine.class).to(RealMachine.class);
    }

}
