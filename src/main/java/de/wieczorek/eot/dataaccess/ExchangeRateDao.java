package de.wieczorek.eot.dataaccess;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.TimedExchangeRate;

public class ExchangeRateDao {

	private CexAPI api;

	public List<TimedExchangeRate> getHistoryEntries(ExchangableType from, ExchangableType to, int hours, int maxValue)
			throws JSONException {
		String json = api.chart(from, to, hours, maxValue);
		List<TimedExchangeRate> result = new ArrayList<>();
		if (json != null) {
			JSONArray array = new JSONArray(json);
			JSONObject dataset = null;
			
			for (int i = 0; !array.isNull(i); i++) {
				dataset = array.getJSONObject(i);
				result.add(new TimedExchangeRate(from, to, dataset.getDouble("price"), LocalDateTime.ofInstant(Instant.ofEpochSecond(dataset.getLong("tmsp")), ZoneId.of("GMT"))));

			}
		}
		return result;
	}

	public ExchangeRateDao(CexAPI api) {
		this.api = api;
	}

	public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to) throws JSONException {
		String json = api.lastPrice(from, to);
		JSONObject dataset = new JSONObject(json);
		double value = Double.parseDouble((String) dataset.get("lprice"));
		return new TimedExchangeRate(from, to, value, LocalDateTime.now());
		
	}
}
