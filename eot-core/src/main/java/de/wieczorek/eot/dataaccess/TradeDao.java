package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.json.JSONException;

import de.wieczorek.eot.business.trade.impl.OrderBo;
import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;

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

    public void performOrder(OrderBo order)
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
