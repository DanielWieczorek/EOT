package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

public interface IAccount {
    ExchangableSet countAllExchangablesOfType(final ExchangableType type);

    void withdraw(final ExchangableSet from);

    void deposit(final ExchangableSet to);

    void clear();
}
