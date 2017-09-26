package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;

/**
 * UC for retrieving information about the currency pairs.
 * 
 * @author Daniel Wieczorek
 *
 */
public class CurrencyPairDao {
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ExchangeRateDao.class.getName());

    /**
     * instance of helper class to access the API of cex.io.
     */
    private final IExchangeApi exchange;

    /**
     * Constructor.
     *
     * @param exchangeInput
     *            Connector for the rest API calls
     */
    @Inject
    public CurrencyPairDao(final IExchangeApi exchangeInput) {
	this.exchange = exchangeInput;
    }

    /**
     * Retrieves the lot size for a given exchangable pair.
     * 
     * @param pair
     *            the pair to retrieve the lot size for
     * @return the lot size as double
     * @throws IOException
     *             When something goes wrong regarding the connection.
     * @throws JSONException
     *             if JSON string returned from the exchange API does not have
     *             the expected format.
     */
    public final Optional<Double> getLotSize(final ExchangablePair pair) {
	LOGGER.info("Retrieving lot size for pair " + pair.getFrom().name() + "/" + pair.getTo().name());
	String json;
	try {
	    json = exchange.getAssetPairInfo(pair);

	    LOGGER.info(json);
	    final JSONObject obj = new JSONObject(json);
	    final double arr = ((JSONObject) ((JSONObject) obj.get("result")).get("XETHXXBT"))
		    .getDouble("lot_multiplier");
	    return Optional.of(arr);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return Optional.empty();
    }
}
