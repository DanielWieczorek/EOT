package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONException;

import de.wieczorek.eot.business.trade.impl.OrderBo;
import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;

/**
 * DAO which performs the actual trade.
 * 
 * @author Daniel Wieczorek
 *
 */
public class TradeDao {
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
    public TradeDao(final IExchangeApi exchangeInput) {
	this.exchange = exchangeInput;
    }

    /**
     * Performs the given order.
     * 
     * @param order
     *            the BO describing the order to be performed
     * @throws IOException
     *             when the communication with the exchange went wrong
     * @throws JSONException
     *             when the returned JSON string was not formatted as expected.
     * @throws InvalidKeyException
     *             autentication error
     * @throws NoSuchAlgorithmException
     *             autentication error
     */
    public final void performOrder(final OrderBo order)
	    throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException {
	LOGGER.info("peforming order:" + " " + order.getType().name() + " " + order.getVolume() + " "
		+ order.getPair().getFrom().name() + "/" + order.getPair().getTo().name() + " @ " + order.getPrice());

	String json = exchange.performOrder(order);
	// final JSONObject obj = new JSONObject(json);
	// final double arr = ((JSONObject) ((JSONObject)
	// obj.get("result")).get("XETHXXBT")).getDouble("lot_multiplier");
	LOGGER.info(json);
    }
}
