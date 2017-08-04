package de.wieczorek.eot;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.log.Log;

import de.wieczorek.eot.ui.rest.MachineInfoService;
import de.wieczorek.eot.ui.rest.StartMachineService;
import de.wieczorek.eot.ui.rest.StopMachineService;

public class Application {

    public static void main(String[] args) throws Exception {
	Log.setLog(new JavaUtilLog());
	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	context.setContextPath("/");
	Server jettyServer = new Server(8100);
	jettyServer.setHandler(context);
	ServletHolder startServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
	startServlet.setInitOrder(0);
	startServlet.setInitParameter("jersey.config.server.provider.classnames",
		StartMachineService.class.getCanonicalName() + ", " //
			+ StopMachineService.class.getCanonicalName() + ", "//
			+ MachineInfoService.class.getCanonicalName());

	LogManager.getLogManager().getLogger("").setLevel(Level.ALL);
	LogManager.getLogManager().getLogger("org.eclipse.jetty.util.log.javautil").setLevel(Level.OFF);
	Enumeration<String> loggers = LogManager.getLogManager().getLoggerNames();
	while (loggers.hasMoreElements())
	    System.out.println(loggers.nextElement());

	try {
	    jettyServer.start();
	    jettyServer.join();
	} finally {
	    jettyServer.destroy();
	}
    }

}
