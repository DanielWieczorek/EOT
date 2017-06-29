package de.wieczorek.eot.domain.trader;

import java.util.List;

import javax.inject.Inject;

import de.wieczorek.eot.business.IBusinessLayerFacade;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

public class SynchronizingAccount extends Account {

    private IBusinessLayerFacade businessLayer;

    @Inject
    public SynchronizingAccount(IBusinessLayerFacade businessLayer) {
	super();
	this.businessLayer = businessLayer;
    }

    @Override
    public final synchronized ExchangableSet countAllExchangablesOfType(final ExchangableType type) {
	updateAmounts();
	return super.countAllExchangablesOfType(type);
    }

    private void updateAmounts() {
	List<ExchangableSet> result = businessLayer.getAccountBalance();
	clear();
	for (ExchangableSet item : result) {
	    if (item.getExchangable().equals(ExchangableType.BTC)) // TODO
		item.setAmount(round(item.getAmount(), 4));
	    if (item.getExchangable().equals(ExchangableType.ETH))
		item.setAmount(round(item.getAmount(), 2));
	    this.content.put(item.getExchangable(), item);
	}
    }

    private double round(double n, int decimals) {
	return Math.floor(n * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

}
