package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

public class AccountBalanceDao {

    private final IExchangeApi exchange;

    @Inject
    public AccountBalanceDao(IExchangeApi exchange) {
	this.exchange = exchange;
    }

    public List<ExchangableSet> getAccountBalance() {
	List<ExchangableSet> result = new ArrayList<>();
	try {

	    String balanceJson = exchange.getAccountBalance();

	    JSONObject obj = new JSONObject(balanceJson);
	    obj = ((JSONObject) obj.get("result"));

	    for (String str : JSONObject.getNames(obj)) {
		ExchangableSet item = new ExchangableSet();
		item.setAmount(obj.getDouble(str));
		item.setExchangable(convertNameToExchangableType(str));
		if (item.getExchangable() != null)
		    result.add(item);
	    }

	    System.out.println(balanceJson);
	} catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return result;

    }

    private ExchangableType convertNameToExchangableType(String str) {
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
