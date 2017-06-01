package de.wieczorek.eot.domain.trader;

import java.util.HashMap;
import java.util.Map;

import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

/**
 * Account which holds all exchangables for a certain trader.
 * 
 * @author Daniel Wieczorek
 *
 */
public class Account implements IAccount {

    /**
     * store for the exchangables and their amounts. They are separated also by
     * the price they have been obtained at.
     */
    protected Map<ExchangableType, ExchangableSet> content;

    /**
     * Constructor.
     */
    public Account() {
	clear();
    }

    /**
     * counts all exchangables of the of the same type. No matter at which price
     * they were obtained.
     * 
     * @param type
     *            the type of exchangable.
     * @return an exchangable set containing all exchangables of the given type
     */
    @Override
    public synchronized ExchangableSet countAllExchangablesOfType(final ExchangableType type) {
	return content.get(type);
    }

    /**
     * removes a given amount of exchangables.
     * 
     * @param from
     *            type and amount of exchangables.
     */
    @Override
    public final void withdraw(final ExchangableSet from) {
	ExchangableSet exchangables = content.get(from.getExchangable());
	exchangables.setAmount(exchangables.getAmount() - from.getAmount());
    }

    /**
     * Adds a given amount of exchangables to the account.
     * 
     * @param to
     *            the amount of exchangables to add.
     */
    @Override
    public final void deposit(final ExchangableSet to) {
	ExchangableSet currentAmount = content.get(to.getExchangable());
	currentAmount.setAmount(currentAmount.getAmount() + to.getAmount());

    }

    /**
     * Removes all exchangables from the account.
     */
    @Override
    public final void clear() {
	content = new HashMap<>();
	content.put(ExchangableType.BTC, new ExchangableSet(ExchangableType.BTC, 0.0));
	content.put(ExchangableType.ETH, new ExchangableSet(ExchangableType.ETH, 0.0));
    }

}
