package de.wieczorek.eot.business.trade.impl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.json.JSONException;

import de.wieczorek.eot.business.trade.ITradeUc;
import de.wieczorek.eot.dataaccess.CurrencyPairDao;
import de.wieczorek.eot.dataaccess.TradeDao;
import de.wieczorek.eot.domain.exchange.Order;

/**
 * Class which contains all the logic to execute orders.
 * 
 * @author Daniel Wieczorek
 *
 */
public class TradeUcImpl implements ITradeUc {

    /**
     * The DAO to retrieve the data for a currency pair. Used to retrieve the
     * lot for a currency pair.
     */
    private CurrencyPairDao currencyPairDao;

    private TradeDao tradeDao;

    /**
     * Constructor.
     * 
     * @param currencyPairDaoInput
     *            currency pair dao.
     */
    @Inject
    public TradeUcImpl(final CurrencyPairDao currencyPairDaoInput, TradeDao tradeDaoInput) {
	this.currencyPairDao = currencyPairDaoInput;
	this.tradeDao = tradeDaoInput;
    }

    @Override
    public final void perform(final Order order) {
	try {
	    OrderBo orderBo = buildOrderBo(order);
	    tradeDao.performOrder(orderBo);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvalidKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Constructs an {@link OrderBo} from the given {@link Order} object.
     * Returns null if something went wrong, if either the exchange could not be
     * contacted to get the lot size or if the returned JSON could not be
     * parsed.
     * 
     * @param order
     *            the order which is converted to the OrderBo
     * @return An OrderBo which is suitable for the underlying dao
     */
    private OrderBo buildOrderBo(final Order order) {
	OrderBo result = null;
	OrderBo intermediateResult = new OrderBo();
	intermediateResult.setExecutionType(OrderExecutionType.limit);
	intermediateResult.setType(order.getType());
	intermediateResult.setPair(order.getPair());
	intermediateResult.setVolume(0.0);
	intermediateResult.setPrice(order.getPrice());
	try {
	    intermediateResult.setVolume(order.getAmount() / currencyPairDao.getLotSize(order.getPair()));
	    result = intermediateResult;
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return result;
    }

}
