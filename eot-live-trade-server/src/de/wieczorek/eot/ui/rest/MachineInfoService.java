package de.wieczorek.eot.ui.rest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.wieczorek.eot.dataaccess.Session;
import de.wieczorek.eot.domain.evolution.IIndividual;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.machine.IMachine;
import de.wieczorek.eot.domain.machine.RealMachine;
import de.wieczorek.eot.domain.trader.Trader;

@Path("/info")
public class MachineInfoService {
    private IMachine machine;

    public MachineInfoService() {
	Session.create("", "VMzovKgqf0B+WqJPwZBp/SU/Kym8IGOK4rcWf4pMZURAIlUjCjnF7YUO",
		"n2TsjqvB9zKgTOxi9htyXtiHGdzkmNj8eg7FkkEHpFTX46aFolrVYenIqtEzw+cEnW1Rbj5mwRxeaba2wJeWyg==");
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
    }

    @GET
    @Produces("application/json")
    @Path("/test")
    public Response test() {

	return Response.status(200).header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Credentials", "true")
		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.entity("{ \"name\": \"Trader1\",\"btc\" : 0.00001,\"eth\" : 23.0,\"netProfit\": 0.51,\"sellsAtLoss\": 12,\"netProfitPercent\": 50.1,\"numberOfTrades\": 23}")
		.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String sayXMLHello() throws JAXBException {
	MachineInfo info = new MachineInfo();
	info.setState(((RealMachine) machine).getState());

	List<IndividualInfo> individualInfoList = new ArrayList<>();

	for (IIndividual individual : ((RealMachine) machine).getTraders().getAll()) {
	    IndividualInfo individualInfo = new IndividualInfo();
	    individualInfo.setBtc(
		    ((Trader) individual).getAccount().countAllExchangablesOfType(ExchangableType.BTC).getAmount());
	    individualInfo.setEth(
		    ((Trader) individual).getAccount().countAllExchangablesOfType(ExchangableType.ETH).getAmount());
	    individualInfo.setName(individual.getName());
	    individualInfo.setNetProfit(((Trader) individual).getPerformance().getNetProfit());
	    individualInfo.setNetProfitPercent(((Trader) individual).getPerformance().getNetProfitPercent());
	    individualInfo.setNumberOfTrades(((Trader) individual).getPerformance().getNumberOfTrades());
	    individualInfo.setSellsAtLoss(((Trader) individual).getPerformance().getNumberOfTradesWithLosses());
	    individualInfoList.add(individualInfo);
	}

	info.setPopulation(individualInfoList);

	java.io.StringWriter sw = new StringWriter();
	JAXBContext jaxbContext = JAXBContext.newInstance(MachineInfo.class);
	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	jaxbMarshaller.marshal(info, sw);
	return sw.toString();
    }

}
