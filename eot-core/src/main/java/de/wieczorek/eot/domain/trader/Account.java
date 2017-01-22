package de.wieczorek.eot.domain.trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

/**
 * Account which holds all exchangables for a certain trader.
 * 
 * @author Daniel Wieczorek
 *
 */
public class Account {

    /**
     * store for the exchangables and their amounts. They are separated also by
     * the price they have been obtained at.
     */
    private Map<ExchangableType, List<ExchangableAmount>> content;

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
    public final synchronized ExchangableSet countAllExchangablesOfType(final ExchangableType type) {
	List<ExchangableAmount> exchangables = content.get(type);
	ExchangableSet result = new ExchangableSet();
	result.setExchangable(type);

	for (ExchangableAmount item : exchangables) {
	    result = result.mergeWith(item.getExchangableAmount());
	}
	return result;
    }

    /**
     * removes a given amount of exchangables.
     * 
     * @param from
     *            type and amount of exchangables.
     */
    public final void withdraw(final ExchangableSet from) {
	List<ExchangableAmount> exchangables = content.get(from.getExchangable());
	double remainingAmount = from.getAmount();
	for (ExchangableAmount item : exchangables) {
	    if (item.getExchangableAmount().getAmount() < remainingAmount) {
		remainingAmount -= item.getExchangableAmount().getAmount();
		item.getExchangableAmount().setAmount(0.0);
		// TODO remove elements;
	    } else {
		item.getExchangableAmount().setAmount(item.getExchangableAmount().getAmount() - remainingAmount);
		break;
	    }
	}

    }

    /**
     * Adds a given amount of exchangables to the account.
     * 
     * @param to
     *            the amount of exchangables to add.
     */
    public final void deposit(final ExchangableAmount to) {
	content.get(to.getExchangableAmount().getExchangable()).add(to);

    }

    /**
     * Removes all exchangables from the account.
     */
    public final void clear() {
	content = new HashMap<>();
	content.put(ExchangableType.BTC, new ArrayList<>());
	content.put(ExchangableType.ETH, new ArrayList<>());
    }

}
