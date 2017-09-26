package de.wieczorek.eot.ui.rest;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.wieczorek.eot.dataaccess.Session;
import de.wieczorek.eot.domain.machine.IMachine;
import de.wieczorek.eot.domain.machine.VirtualMachine;
import de.wieczorek.eot.domain.trader.Trader;

@Path("/trader")
public class RequestTraderService {
    private IMachine machine;

    public RequestTraderService() {
	Session.create("up103150124", "gAmCFnV9w4RpqkUXkjm6r7950g0", "m9iW5OmwHS5dR5IiwULHvKbFiY");
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public String sayHtmlHello(@PathParam("id") int id) throws JAXBException {
	Trader t = (Trader) ((VirtualMachine) machine).getTraders().getAll().get(id);

	java.io.StringWriter sw = new StringWriter();
	JAXBContext jaxbContext = JAXBContext.newInstance(Trader.class);
	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	jaxbMarshaller.marshal(t, sw);

	return sw.toString();
    }

}
