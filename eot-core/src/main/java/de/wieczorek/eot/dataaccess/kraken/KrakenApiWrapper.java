package de.wieczorek.eot.dataaccess.kraken;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import de.wieczorek.eot.business.configuration.exchange.IExchangeConfigurationUc;
import de.wieczorek.eot.business.trade.impl.OrderBo;
import de.wieczorek.eot.business.trade.impl.OrderExecutionType;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import edu.self.kraken.api.KrakenApi;
import edu.self.kraken.api.KrakenApi.Method;

public class KrakenApiWrapper implements IExchangeApi {

    private KrakenApi api;
    private Map<ExchangableType, String> exchangableConversionMap;

    @Inject
    public KrakenApiWrapper(KrakenApi api, IExchangeConfigurationUc exchangeConfigUc) {
	this.api = api;
	exchangableConversionMap = new HashMap<ExchangableType, String>();
	exchangableConversionMap.put(ExchangableType.BTC, "XBT");
	api.setKey(exchangeConfigUc.getKey());
	api.setSecret(exchangeConfigUc.getSecret());
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

    @Override
    public String getAccountBalance() throws InvalidKeyException, NoSuchAlgorithmException, IOException {
	return api.queryPrivate(Method.BALANCE);

    }

    @Override
    public String getAssetPairInfo(ExchangablePair pair) throws IOException {
	Map<String, String> input = new HashMap<>();

	input.put("pair", convertExchangableType(pair.getFrom()) + convertExchangableType(pair.getTo()));
	return api.queryPublic(Method.ASSET_PAIRS, input);
    }

    @Override
    public String performOrder(OrderBo order) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
	Map<String, String> input = new HashMap<>();

	input.put("pair",
		convertExchangableType(order.getPair().getFrom()) + convertExchangableType(order.getPair().getTo()));
	input.put("type", order.getType().name().toLowerCase());
	input.put("ordertype", order.getExecutionType().name());
	input.put("volume", order.getVolume() + "");
	if (order.getExecutionType().equals(OrderExecutionType.limit))
	    input.put("price", order.getPrice() + "");
	input.put("expiretm", "+55");
	input.put("trading_agreement", "agree");
	return api.queryPrivate(Method.ADD_ORDER, input);
    }

}
