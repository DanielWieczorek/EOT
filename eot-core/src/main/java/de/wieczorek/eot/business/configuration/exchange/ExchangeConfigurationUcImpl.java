package de.wieczorek.eot.business.configuration.exchange;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * implementation of {@link IExchangeConfigurationUc}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ExchangeConfigurationUcImpl implements IExchangeConfigurationUc {

    /**
     * Object for accessing the configuration.
     */
    private Configuration exchangeConfiguration;

    /**
     * Constructor.
     */
    public ExchangeConfigurationUcImpl() {
	final Configurations configs = new Configurations();
	try {
	    exchangeConfiguration = configs.properties(new File("exchange.properties"));

	} catch (final ConfigurationException cex) {
	    // TODO: log exception
	    cex.printStackTrace();
	    exchangeConfiguration = null;
	}
    }

    @Override
    public final String getKey() {
	return exchangeConfiguration.getString("exchange.key");
    }

    @Override
    public final String getSecret() {
	return exchangeConfiguration.getString("exchange.secret");
    }
}
