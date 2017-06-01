package de.wieczorek.eot.business.account;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;

public interface IAccountBalanceUc {
    List<ExchangableSet> getAccountBalance();
}
