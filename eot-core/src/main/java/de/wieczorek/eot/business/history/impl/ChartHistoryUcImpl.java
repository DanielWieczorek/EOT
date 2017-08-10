package de.wieczorek.eot.business.history.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
	ExchangeRateHistory result = new ExchangeRateHistory();
	Optional<List<ExchangeRateBo>> bos = dao.getDetailedHistoryEntries(from, to, minutes);
	while (!bos.isPresent()) {
	    bos = dao.getDetailedHistoryEntries(from, to, minutes);
	}

	dao.saveHistoryEntries(bos.get());
	final List<TimedExchangeRate> ter = new ArrayList<>();
	for (final ExchangeRateBo item : dao.getDetailedHistoryEntriesFromDb(from, to, minutes)) {
	    ter.add(new TimedExchangeRate(item.getKey().getFromCurrency(), item.getKey().getToCurrency(),
		    item.getExchangeRate(),
		    LocalDateTime.ofInstant(Instant.ofEpochSecond(item.getKey().getTimestamp()), ZoneId.of("GMT"))));
	}
	result = ExchangeRateHistory.from(ter);
	return result;
    }
}
