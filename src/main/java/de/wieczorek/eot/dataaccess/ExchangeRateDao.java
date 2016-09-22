package de.wieczorek.eot.dataaccess;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.impetus.client.cassandra.common.CassandraConstants;

import de.wieczorek.eot.business.bo.ExchangeRateBo;
import de.wieczorek.eot.business.bo.ExchangeRateBoKey;
import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.TimedExchangeRate;

public class ExchangeRateDao {

	private CexAPI api;
	private EntityManager entityManager = null;

	public List<TimedExchangeRate> getHistoryEntries(ExchangableType from, ExchangableType to, int hours, int maxValue)
			throws JSONException {
		String json = api.chart(from, to, hours, maxValue);
		List<TimedExchangeRate> result = new ArrayList<>();
		if (json != null) {
			JSONArray array = new JSONArray(json);
			JSONObject dataset = null;

			for (int i = 0; !array.isNull(i); i++) {
				dataset = array.getJSONObject(i);
				result.add(new TimedExchangeRate(from, to, dataset.getDouble("price"),
						LocalDateTime.ofInstant(Instant.ofEpochSecond(dataset.getLong("tmsp")), ZoneId.of("GMT"))));

			}
		}
		return result;
	}

	/**
	 * TODO use hours.
	 * 
	 * @param from
	 * @param to
	 * @param hours
	 * @return
	 * @throws JSONException
	 */
	public List<ExchangeRateBo> getDetailedHistoryEntries(ExchangableType from, ExchangableType to, int hours)
			throws JSONException {
		List<ExchangeRateBo> entries = new ArrayList<ExchangeRateBo>();
		int days = Math.max(1, hours / 24);
		for (int x = 0; x < days; x++) {
			String result = api.ohclv(from, to,
					LocalDateTime.now().minusDays(x + 1).format(DateTimeFormatter.BASIC_ISO_DATE));
			System.out.println(result);

			JSONObject obj = new JSONObject(result);
			String array = (String) obj.get("data1m");
			JSONArray arr = new JSONArray(array);
			for (int i = 0; i < arr.length(); i++) {
				JSONArray item = (JSONArray) arr.get(i);

				System.out.println("Timestamp:" + Date.from(Instant.ofEpochSecond(item.getLong(0))) + "\t open: "
						+ item.getDouble(1) + "\t high: " + item.getDouble(2) + "\t low: " + item.getDouble(3)
						+ "\t close: " + item.getDouble(4) + "\t volume: " + item.getDouble(5));

				ExchangeRateBo rate = new ExchangeRateBo();
				rate.setExchangeRate(item.getDouble(4));
				rate.setKey(new ExchangeRateBoKey(item.getLong(0), ExchangableType.ETH, ExchangableType.BTC));

				if (!entries.isEmpty()) {
					ExchangeRateBo lastExchangeRate = entries.get(entries.size() - 1);

					int entriesToInsert = (int) ((item.getLong(0) - lastExchangeRate.getKey().getTimestamp()) / 60);
					for (int j = 1; j < entriesToInsert; j++) {
						ExchangeRateBo insertRate = new ExchangeRateBo();
						insertRate.setExchangeRate(lastExchangeRate.getExchangeRate());
						insertRate.setKey(new ExchangeRateBoKey(lastExchangeRate.getKey().getTimestamp() + 60 * j,
								ExchangableType.ETH, ExchangableType.BTC));
						entries.add(insertRate);
					}
				}

				entries.add(rate);
			}
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	public List<ExchangeRateBo> getDetailedHistoryEntriesFromDb(ExchangableType from, ExchangableType to, int hours) {
		Query q = entityManager.createQuery("Select p from ExchangeRateBo p where p.key.timestamp <="
				+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + " and p.key.timestamp >= "
				+ LocalDateTime.now().minusHours(hours).toEpochSecond(ZoneOffset.UTC));
		q.setMaxResults(Integer.MAX_VALUE);
		List<ExchangeRateBo> result = q.getResultList();
		return result;
	}

	public void saveHistoryEntries(List<ExchangeRateBo> entries) throws JSONException {
		entityManager.setFlushMode(FlushModeType.COMMIT);
		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		for (ExchangeRateBo entry : entries)
			entityManager.persist(entry);
		transaction.commit();
	}

	public ExchangeRateDao(CexAPI api) {
		this.api = api;
		Map<String, String> props = new HashMap<String, String>();
		props.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu", props);
		this.entityManager = emf.createEntityManager();

	}

	public TimedExchangeRate getCurrentExchangeRate(ExchangableType from, ExchangableType to) throws JSONException {
		String json = api.lastPrice(from, to);
		JSONObject dataset = new JSONObject(json);
		double value = Double.parseDouble((String) dataset.get("lprice"));
		return new TimedExchangeRate(from, to, value, LocalDateTime.now());
	}
}
