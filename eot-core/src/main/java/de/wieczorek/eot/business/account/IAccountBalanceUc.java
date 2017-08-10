package de.wieczorek.eot.business.account;

import java.util.List;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;

/**
 * Usecase for retrieving the current account balance.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IAccountBalanceUc {
    /**
     * Returns all available amounts of the exchangables which are known to this
     * application.
     * 
     * @return a list of exchangable sets which specify the amount.
     */
    List<ExchangableSet> getAccountBalance();
}
