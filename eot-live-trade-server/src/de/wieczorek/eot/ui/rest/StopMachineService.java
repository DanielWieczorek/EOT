package de.wieczorek.eot.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.eot.dataaccess.Session;
import de.wieczorek.eot.domain.machine.IMachine;

@Path("/stop")
public class StopMachineService {
    private IMachine machine;

    public StopMachineService() {
	Session.create("up103150124", "gAmCFnV9w4RpqkUXkjm6r7950g0", "m9iW5OmwHS5dR5IiwULHvKbFiY");
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String sayHtmlHello() {
	machine.stop();
	return "<html> " + "<title>" + "Hello Jersey" + "</title>" + "<body><h1>" + "machine stopped" + "</body></h1>"
		+ "</html> ";
    }

}
