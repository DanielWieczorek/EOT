package de.wieczorek.eot.ui.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.machine.IMachine;
import de.wieczorek.eot.domain.machine.RealMachine;
import de.wieczorek.eot.domain.trader.IAccount;
import de.wieczorek.eot.domain.trader.Trader;
import de.wieczorek.eot.ui.trader.TraderConfiguration;
import de.wieczorek.eot.ui.trader.TraderFactory;

@Path("/import/")
public class ImportTraderService {

    private static final Logger logger = Logger.getLogger(ImportTraderService.class.getName());

    private IMachine machine;
    private IAccount wallet;
    private IExchange exchange;

    public ImportTraderService() {
	machine = InjectorSingleton.getInjector().getInstance(IMachine.class);
	wallet = InjectorSingleton.getInjector().getInstance(IAccount.class);
	exchange = InjectorSingleton.getInjector().getInstance(IExchange.class);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public String sayHtmlHello(TraderConfiguration config) throws JAXBException {

	Trader trader = TraderFactory.createTrader(config, wallet, exchange);
	((RealMachine) machine).getTraders().clearPopulation();
	((RealMachine) machine).addTrader(trader);
	logger.fatal("imported trader: " + trader.generateDescriptiveName());
	return "ok";
    }
}
