package de.wieczorek.eot.dataaccess;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
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

    /**
     * The EM factory for the access to the DB.
     */
    private EntityManagerFactory emf;

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ExchangeRateDao.class.getName());

    /**
     * instance of helper class to access the API of cex.io.
     */
    private final IExchangeApi exchange;

    /**
     * Calls the web service for the history and then converts the returned JSON
     * to a list of exchange rates. The history has 1 entry for each minute
     * within the specified range.
     *
     * @param from
     *            source currency
     * @param to
     *            target currency
     * @param minutes
     *            number of minutes for which to return the exchange rate
     *            history
     * @return a list of exchange rates
     * @throws JSONException
     *             if the JSON string cannot be parsed
     * @throws IOException
     *             if there are problems regarding the connection to the
     *             exchange
     */
    public final Optional<List<ExchangeRateBo>> getDetailedHistoryEntries(final ExchangableType from,
	    final ExchangableType to, final int minutes) {
	LOGGER.info("Retrieving detailed history for " + from.name() + "/" + to.name() + " with length of " + minutes
		+ " minutes");
	final List<ExchangeRateBo> entries = new ArrayList<>();
	final int openingPrice = 1;
	final int maxPrice = 2;
	final int minPrice = 3;
	final int closePriceIndex = 4;
	final int volumeIndex = 6;
	final int secondsPerMinute = 60;

	final ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
	final long startTime = LocalDateTime.now().minusMinutes(minutes).toEpochSecond(offset);
	LOGGER.log(Level.INFO, LocalDateTime.now().minusMinutes(minutes).toString());
	String result;
	try {
	    result = exchange.ohclv(from, to, startTime);

	    LOGGER.log(Level.INFO, result);

	    final JSONObject obj = new JSONObject(result);
	    final JSONArray arr = (JSONArray) ((JSONObject) obj.get("result")).get("XETHXXBT");
	    for (int i = 0; i < arr.length(); i++) {
		final JSONArray item = (JSONArray) arr.get(i);
		LOGGER.log(Level.INFO,
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
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return Optional.empty();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return Optional.empty();
	}
	return Optional.of(entries);
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
     * @param minutes
     *            number of minutes for which to return the exchange rate
     *            history
     * @return a list of exchange rates
     * @throws JSONException
     *             if the JSON string cannot be parsed
     */
    @SuppressWarnings("unchecked")
    public final List<ExchangeRateBo> getDetailedHistoryEntriesFromDb(final ExchangableType from,
	    final ExchangableType to, final int minutes) {
	EntityManager entityManager = emf.createEntityManager();
	final Query q = entityManager.createQuery("Select p from ExchangeRateBo p where p.key.timestamp <="
		+ LocalDateTime.now().toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()))
		+ " and p.key.timestamp >= " + LocalDateTime.now().minusMinutes(minutes)
			.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())));
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
	EntityManager entityManager = emf.createEntityManager();
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
     * @param exchangeInput
     *            Connector for the rest API calls
     */
    @Inject
    public ExchangeRateDao(final IExchangeApi exchangeInput) {
	this.exchange = exchangeInput;
	final Map<String, String> props = new HashMap<>();
	props.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
	emf = Persistence.createEntityManagerFactory("cassandra_pu", props);

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
     *             if the response from the server could not be parsed
     * @throws IOException
     *             if the communication with the exchange failed.
     */
    public final Optional<TimedExchangeRate> getCurrentExchangeRate(final ExchangableType from,
	    final ExchangableType to) {
	double value = 0.0;
	String json;
	try {
	    json = exchange.lastPrice(from, to);

	    final JSONObject obj = new JSONObject(json);
	    final JSONArray arr = (JSONArray) ((JSONObject) ((JSONObject) obj.get("result")).get("XETHXXBT")).get("c");
	    value = arr.getDouble(0);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return Optional.empty();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return Optional.empty();
	}
	return Optional.of(new TimedExchangeRate(from, to, value, LocalDateTime.now()));
    }
}
