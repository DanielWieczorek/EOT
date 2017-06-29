package de.wieczorek.eot.business.history.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONException;

import de.wieczorek.eot.business.bo.ExchangeRateBo;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.dataaccess.ExchangeRateDao;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

/**
 * implementation of {@link IChartHistoryUc}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class ChartHistoryUcImpl implements IChartHistoryUc {

    /**
     * dao to retrieve the information from the API and the database.
     */
    private final ExchangeRateDao dao;

    /**
     * Constructor.
     *
     * @param daoToSet
     *            the dao needed for data access
     */
    @Inject
    public ChartHistoryUcImpl(final ExchangeRateDao daoToSet) {
	this.dao = daoToSet;
    }

    @Override
    public final ExchangeRateHistory getDetailedHistoryFromDb(final ExchangableType from, final ExchangableType to,
	    final int minutes) {
	ExchangeRateHistory result = null;
	final List<ExchangeRateBo> bos;
	try {
	    bos = dao.getDetailedHistoryEntries(from, to, minutes);

	    dao.saveHistoryEntries(bos);
	    final List<TimedExchangeRate> ter = new ArrayList<>();
	    for (final ExchangeRateBo item : dao.getDetailedHistoryEntriesFromDb(from, to, minutes)) {
		ter.add(new TimedExchangeRate(item.getKey().getFromCurrency(), item.getKey().getToCurrency(),
			item.getExchangeRate(), LocalDateTime
				.ofInstant(Instant.ofEpochSecond(item.getKey().getTimestamp()), ZoneId.of("GMT"))));
	    }
	    result = ExchangeRateHistory.from(ter);
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return result;
    }
}
