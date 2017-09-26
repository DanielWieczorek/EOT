package de.wieczorek.eot.ui.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.machine.IMachine;
import de.wieczorek.eot.domain.trader.Trader;

@Path("/export/")
public class ExportTraderService {
    private IMachine machine;

    public ExportTraderService() {
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id}")
    public String sayHtmlHello(@PathParam("id") long id) {
	IIndividual individual = machine.getTraderById(id);
	return ((Trader) individual).generateDescriptiveName();
    }

}
