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

import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.machine.IMachine;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.trader.TraderConfiguration;
import de.wieczorek.eot.ui.trader.TraderConfigurationFactory;

@Path("/export/")
public class ExportTraderService {
    private IMachine machine;

    public ExportTraderService() {
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id}")
    public String sayHtmlHello(@PathParam("id") long id) throws JAXBException {
	IIndividual individual = machine.getTraderById(id);
	java.io.StringWriter sw = new StringWriter();
	JAXBContext jaxbContext = JAXBContext.newInstance(TraderConfiguration.class);
	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	jaxbMarshaller.marshal(TraderConfigurationFactory.createTraderConfiguration(((Trader) individual)), sw);
	return sw.toString();
    }
}
