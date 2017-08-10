package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

/**
 * DAO to retrieve the account balance.
 * 
 * @author Daniel Wieczorek
 *
 */
public class AccountBalanceDao {
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ExchangeRateDao.class.getName());

    /**
     * The exchange from which the account balance is retrieved.
     */
    private final IExchangeApi exchange;

    /**
     * Constructor.
     * 
     * @param exchange
     *            the exchange needed for the external calls.
     */
    @Inject
    public AccountBalanceDao(final IExchangeApi exchange) {
	this.exchange = exchange;
    }

    /**
     * Returns the account balance for all known currencies.
     * 
     * @return a list of exchangable sets.
     */
    public final Optional<List<ExchangableSet>> getAccountBalance() {

	List<ExchangableSet> resultList = new ArrayList<>();
	try {
	    LOGGER.info("Retrieving account balance");
	    String balanceJson = exchange.getAccountBalance();
	    LOGGER.info(balanceJson);

	    JSONObject obj = new JSONObject(balanceJson);
	    obj = ((JSONObject) obj.get("result"));

	    for (String str : JSONObject.getNames(obj)) {
		ExchangableSet item = new ExchangableSet();
		item.setAmount(obj.getDouble(str));
		item.setExchangable(convertNameToExchangableType(str));
		if (item.getExchangable() != null) {
		    resultList.add(item);
		}
	    }

	} catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return Optional.empty();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return Optional.empty();
	}

	return Optional.of(resultList);

    }

    /**
     * Converts the name of the exchangable from a string to the corresponding
     * enum type. This is needed because the name of an exchangable may vary
     * among different exchanges.
     * 
     * @param str
     *            name to convert.
     * @return the corresponding enum value or null if no matching value was
     *         found.
     */
    private ExchangableType convertNameToExchangableType(final String str) {
	switch (str) {
	case "ETH":
	case "XETH":
	    return ExchangableType.ETH;
	case "BTC":
	case "XBT":
	case "XXBT":
	    return ExchangableType.BTC;
	default:
	    return null;
	}
    }

}
