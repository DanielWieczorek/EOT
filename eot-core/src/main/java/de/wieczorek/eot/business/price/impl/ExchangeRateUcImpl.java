package de.wieczorek.eot.business.price.impl;

import org.json.JSONException;

import de.wieczorek.eot.business.price.IExchangeRateUc;
import de.wieczorek.eot.dataaccess.ExchangeRateDao;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

/**
 * Implementation of {@link IExchangeRateUc}.
 *
 * @author Daniel Wieczorek
 *
 */
public class ExchangeRateUcImpl implements IExchangeRateUc {

    /**
     * the dao for access to the API.
     */
    private final ExchangeRateDao dao;

    /**
     * Constructor.
     *
     * @param daoInput
     *            dao needed for the access to the API.
     */
    public ExchangeRateUcImpl(final ExchangeRateDao daoInput) {
	this.dao = daoInput;
    }

    @Override
    public final TimedExchangeRate getCurrentExchangeRate(final ExchangableType from, final ExchangableType to) {

	try {
	    return dao.getCurrentExchangeRate(from, to);
	} catch (final JSONException e) {
	    e.printStackTrace();
	}
	return new TimedExchangeRate(from, to, 0.0, null);
    }

}
