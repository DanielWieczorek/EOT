package de.wieczorek.eot;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.wieczorek.eot.ui.rest.MachineInfoService;
import de.wieczorek.eot.ui.rest.StartMachineService;
import de.wieczorek.eot.ui.rest.StopMachineService;

public class Application {

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
			+ MachineInfoService.class.getCanonicalName());
	try {
	    jettyServer.start();
	    jettyServer.join();
	} finally {
	    jettyServer.destroy();
	}
    }

}
