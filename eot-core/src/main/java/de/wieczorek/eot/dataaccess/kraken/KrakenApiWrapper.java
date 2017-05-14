package de.wieczorek.eot.dataaccess.kraken;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import de.wieczorek.eot.domain.exchangable.ExchangableType;
import edu.self.kraken.api.KrakenApi;
import edu.self.kraken.api.KrakenApi.Method;

public class KrakenApiWrapper implements IExchangeApi {

    private KrakenApi api;
    private Map<ExchangableType, String> exchangableConversionMap;

    @Inject
    public KrakenApiWrapper(KrakenApi api) {
	this.api = api;
	exchangableConversionMap = new HashMap<ExchangableType, String>();
	exchangableConversionMap.put(ExchangableType.BTC, "XBT");
    }

    public void setSecret(String secret) {
	api.setSecret(secret);
    }

    public void setApiKey(String apiKey) {
	api.setKey(apiKey);
    }

    public String getTradeBalance(String asset) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
	String response;
	Map<String, String> input = new HashMap<>();

	input.put("asset", asset);
	response = api.queryPrivate(Method.TRADE_BALANCE, input);
	return response;
    }

    @Override
    public String lastPrice(ExchangableType major, ExchangableType minor) throws IOException {
	Map<String, String> input = new HashMap<>();

	input.put("pair", convertExchangableType(major) + convertExchangableType(minor));
	return api.queryPublic(Method.TICKER, input);
    }

    @Override
    public String ohclv(ExchangableType major, ExchangableType minor, long interval) throws IOException {
	Map<String, String> input = new HashMap<>();

	input.put("pair", convertExchangableType(major) + convertExchangableType(minor));
	input.put("interval", "1");
	input.put("since", interval + "");
	return api.queryPublic(Method.OHLC, input);
    }

    private String convertExchangableType(ExchangableType type) {
	String result = exchangableConversionMap.get(type);
	if (result == null) {
	    result = type.name();
	}
	return result;
    }

}
