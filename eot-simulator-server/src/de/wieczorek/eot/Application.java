package de.wieczorek.eot;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.wieczorek.eot.ui.rest.ExportTraderService;
import de.wieczorek.eot.ui.rest.MachineInfoService;
import de.wieczorek.eot.ui.rest.PauseMachineService;
import de.wieczorek.eot.ui.rest.RequestTraderService;
import de.wieczorek.eot.ui.rest.StartMachineService;
import de.wieczorek.eot.ui.rest.StopMachineService;

public class Application {
    Logger logger = Logger.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	context.setContextPath("/");
	Server jettyServer = new Server(8090);
	jettyServer.setHandler(context);
	ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
	jerseyServlet.setInitOrder(0);
	jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
		StartMachineService.class.getCanonicalName() + ", " //
			+ StopMachineService.class.getCanonicalName() + ", "//
			+ MachineInfoService.class.getCanonicalName() + ", "
			+ PauseMachineService.class.getCanonicalName() + ", "
			+ RequestTraderService.class.getCanonicalName() + ","
			+ ExportTraderService.class.getCanonicalName());
	try {
	    jettyServer.start();
	    jettyServer.join();
	} finally {
	    jettyServer.destroy();
	}
    }

}
