package de.wieczorek.eot.business.account.impl;

import java.util.List;

import javax.inject.Inject;

import de.wieczorek.eot.business.account.IAccountBalanceUc;
import de.wieczorek.eot.dataaccess.AccountBalanceDao;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;

public class AccountBalanceUcImpl implements IAccountBalanceUc {

    private AccountBalanceDao dao;

    @Inject
    public AccountBalanceUcImpl(AccountBalanceDao daoToSet) {

	this.dao = daoToSet;
    }

    @Override
    public List<ExchangableSet> getAccountBalance() {
	return dao.getAccountBalance();
    }

}
