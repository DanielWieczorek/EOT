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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
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
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

/**
 * Class which reads the exchange rates from the API and converts it to objects.
 * It can also retrieve the information from the DB.
 *
 * @author Daniel Wieczorek
 *
 */
public class ExchangeRateDao {

    final EntityManagerFactory emf;

    private static final Logger logger = Logger.getLogger(ExchangeRateDao.class.getName());

    /**
     * instance of helper class to access the API of cex.io.
     */
    private final CexAPI api;

    /**
     * Entity manager for the ORM framework to access the DB.
     */
    private EntityManager entityManager = null;

    /**
     * Calls the web service for the history and then converts the returned JSON
     * to a list of exchange rates. The history returned has approximately 1
     * entry per 15 minutes.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param hours
     *            number of hours for which to return the exchange rate history
     * @param maxValue
     *            maximum value of returned entries
     * @return a list of exchange rates
     * @throws JSONException
     *             if the JSON string cannot be parsed
     */
    public final List<TimedExchangeRate> getHistoryEntries(final ExchangableType from, final ExchangableType to,
	    final int hours, final int maxValue) throws JSONException {
	final String json = api.chart(from, to, hours, maxValue);
	final List<TimedExchangeRate> result = new ArrayList<>();
	if (json != null) {
	    final JSONArray array = new JSONArray(json);
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
     * Calls the web service for the history and then converts the returned JSON
     * to a list of exchange rates. The history has 1 entry for each minute
     * within the specified range.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param hours
     *            number of hours for which to return the exchange rate history
     * @return a list of exchange rates
     * @throws JSONException
     *             if the JSON string cannot be parsed
     */
    public final List<ExchangeRateBo> getDetailedHistoryEntries(final ExchangableType from, final ExchangableType to,
	    final int hours) throws JSONException {
	final List<ExchangeRateBo> entries = new ArrayList<>();
	final int days = Math.max(1, hours / 24);
	final int openingPrice = 1;
	final int maxPrice = 2;
	final int minPrice = 3;
	final int closePriceIndex = 4;
	final int volumeIndex = 5;
	final int secondsPerMinute = 60;
	for (int x = 0; x < days; x++) {
	    final String result = api.ohclv(from, to,
		    LocalDateTime.now().minusDays(x + 1).format(DateTimeFormatter.BASIC_ISO_DATE));
	    logger.log(Level.INFO, result);

	    final JSONObject obj = new JSONObject(result);
	    final String array = (String) obj.get("data1m");
	    final JSONArray arr = new JSONArray(array);
	    for (int i = 0; i < arr.length(); i++) {
		final JSONArray item = (JSONArray) arr.get(i);
		logger.log(Level.INFO,
			"Timestamp:" + Date.from(Instant.ofEpochSecond(item.getLong(0))) + "\t open: "
				+ item.getDouble(openingPrice) + "\t high: " + item.getDouble(maxPrice) + "\t low: "
				+ item.getDouble(minPrice) + "\t close: " + item.getDouble(closePriceIndex)
				+ "\t volume: " + item.getDouble(volumeIndex));

		final ExchangeRateBo rate = new ExchangeRateBo();
		rate.setExchangeRate(item.getDouble(closePriceIndex));
		rate.setKey(new ExchangeRateBoKey(item.getLong(0), ExchangableType.ETH, ExchangableType.BTC));

		if (!entries.isEmpty()) {
		    final ExchangeRateBo lastExchangeRate = entries.get(entries.size() - 1);

		    final int entriesToInsert = (int) ((item.getLong(0) - lastExchangeRate.getKey().getTimestamp())
			    / 60);
		    for (int j = 1; j < entriesToInsert; j++) {
			final ExchangeRateBo insertRate = new ExchangeRateBo();
			insertRate.setExchangeRate(lastExchangeRate.getExchangeRate());
			insertRate.setKey(
				new ExchangeRateBoKey(lastExchangeRate.getKey().getTimestamp() + secondsPerMinute * j,
					ExchangableType.ETH, ExchangableType.BTC));
			entries.add(insertRate);
		    }
		}

		entries.add(rate);
	    }
	}
	return entries;
    }

    /**
     * Calls the DB for the history and then converts the returned JSON to a
     * list of exchange rates. The history has 1 entry for each minute within
     * the specified range.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param hours
     *            number of hours for which to return the exchange rate history
     * @return a list of exchange rates
     * @throws JSONException
     *             if the JSON string cannot be parsed
     */
    @SuppressWarnings("unchecked")
    public final List<ExchangeRateBo> getDetailedHistoryEntriesFromDb(final ExchangableType from,
	    final ExchangableType to, final int hours) {
	entityManager = emf.createEntityManager();
	final Query q = entityManager.createQuery("Select p from ExchangeRateBo p where p.key.timestamp <="
		+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + " and p.key.timestamp >= "
		+ LocalDateTime.now().minusHours(hours).toEpochSecond(ZoneOffset.UTC));
	q.setMaxResults(Integer.MAX_VALUE);
	final List<ExchangeRateBo> result = q.getResultList();
	entityManager.close();
	return result;
    }

    /**
     * Saves the given exchange rate history entries in the database.
     *
     * @param entries
     *            entries to save.
     */
    public final void saveHistoryEntries(final List<ExchangeRateBo> entries) {
	entityManager = emf.createEntityManager();
	entityManager.setFlushMode(FlushModeType.COMMIT);
	final EntityTransaction transaction = entityManager.getTransaction();

	transaction.begin();
	for (final ExchangeRateBo entry : entries) {
	    entityManager.persist(entry);
	}
	transaction.commit();
	entityManager.close();
    }

    /**
     * Constructor.
     *
     * @param apiInput
     *            Connector for the rest API calls
     */
    @Inject
    public ExchangeRateDao(final CexAPI apiInput) {
	this.api = apiInput;
	final Map<String, String> props = new HashMap<>();
	props.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
	emf = Persistence.createEntityManagerFactory("cassandra_pu", props);
	this.entityManager = emf.createEntityManager();

    }

    /**
     * Retrieves the current exchange rate via the web service API.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @return the exchange rate for the current date and time.
     * @throws JSONException
     *             if the respsonse from the server could not be parsed
     */
    public final TimedExchangeRate getCurrentExchangeRate(final ExchangableType from, final ExchangableType to)
	    throws JSONException {
	final String json = api.lastPrice(from, to);
	final JSONObject dataset = new JSONObject(json);
	final double value = Double.parseDouble((String) dataset.get("lprice"));
	return new TimedExchangeRate(from, to, value, LocalDateTime.now());
    }
}
