package de.wieczorek.eot.domain.trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wieczorek.eot.domain.exchangable.ExchangableAmount;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

public class Wallet {

    private Map<ExchangableType, List<ExchangableAmount>> content;

    public Wallet() {
	clear();
    }

    public synchronized ExchangableSet countAllExchangablesOfType(ExchangableType type) {
	List<ExchangableAmount> exchangables = content.get(type);
	ExchangableSet result = new ExchangableSet();
	result.setExchangable(type);

	for (ExchangableAmount item : exchangables)
	    result = result.mergeWith(item.getExchangableAmount());

	return result;
    }

    public void withdraw(ExchangableSet from) {
	List<ExchangableAmount> exchangables = content.get(from.getExchangable());
	double remainingAmount = from.getAmount();
	for (ExchangableAmount item : exchangables)
	    if (item.getExchangableAmount().getAmount() < remainingAmount) {
		remainingAmount -= item.getExchangableAmount().getAmount();
		item.getExchangableAmount().setAmount(0.0);
		// TODO remove elements;
	    } else {
		item.getExchangableAmount().setAmount(item.getExchangableAmount().getAmount() - remainingAmount);
		break;
	    }

    }

    public void deposit(ExchangableAmount to) {
	content.get(to.getExchangableAmount().getExchangable()).add(to);

    }

    public void clear() {
	content = new HashMap<>();
	content.put(ExchangableType.BTC, new ArrayList<>());
	content.put(ExchangableType.ETH, new ArrayList<>());
    }

}
