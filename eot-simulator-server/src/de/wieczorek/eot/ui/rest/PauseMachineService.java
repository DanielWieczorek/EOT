package de.wieczorek.eot.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.eot.dataaccess.Session;
import de.wieczorek.eot.domain.machine.IMachine;

@Path("/pause")
public class PauseMachineService {
    private IMachine machine;

    public PauseMachineService() {
	Session.create("up103150124", "gAmCFnV9w4RpqkUXkjm6r7950g0", "m9iW5OmwHS5dR5IiwULHvKbFiY");
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String sayXMLHello() {
	machine.pause();
	return "<?xml version=\"1.0\"?>" + "<hello> machine paused" + "</hello>";
    }

}
