package de.wieczorek.eot.business.account.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import de.wieczorek.eot.business.account.IAccountBalanceUc;
import de.wieczorek.eot.dataaccess.AccountBalanceDao;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;

/**
 * Implementation of {@link IAccountBalanceUc}.
 * 
 * @author Daniel Wieczorek
 *
 */
public class AccountBalanceUcImpl implements IAccountBalanceUc {

    /**
     * The DAO to retrieve the data.
     */
    private AccountBalanceDao dao;

    /**
     * Constructor.
     * 
     * @param daoToSet
     *            the DAO which is used to retrieve the data.
     */
    @Inject
    public AccountBalanceUcImpl(final AccountBalanceDao daoToSet) {

	this.dao = daoToSet;
    }

    @Override
    public final List<ExchangableSet> getAccountBalance() {
	Optional<List<ExchangableSet>> result = dao.getAccountBalance();
	while (!result.isPresent()) {
	    result = dao.getAccountBalance();
	}
	return result.get();
    }

}
