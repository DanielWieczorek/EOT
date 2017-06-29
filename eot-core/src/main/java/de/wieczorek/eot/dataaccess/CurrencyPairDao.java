package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;

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

    public double getLotSize(ExchangablePair pair) throws IOException, JSONException {
	LOGGER.info("Retrieving lot size for pair " + pair.getFrom().name() + "/" + pair.getTo().name());
	String json = exchange.getAssetPairInfo(pair);
	LOGGER.info(json);
	final JSONObject obj = new JSONObject(json);
	final double arr = ((JSONObject) ((JSONObject) obj.get("result")).get("XETHXXBT")).getDouble("lot_multiplier");
	return arr;
    }
}
