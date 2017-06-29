import java.io.IOException;

import org.json.JSONException;

import de.wieczorek.eot.business.trade.impl.TradeUcImpl;
import de.wieczorek.eot.dataaccess.CurrencyPairDao;
import de.wieczorek.eot.dataaccess.TradeDao;
import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.dataaccess.kraken.KrakenApiWrapper;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchange.Order;
import de.wieczorek.eot.domain.exchange.OrderType;
import edu.self.kraken.api.KrakenApi;

public class TestDriver {

    public static void main(String[] args) throws IOException, JSONException {
	KrakenApi krakenApi = new KrakenApi();
	IExchangeApi api = new KrakenApiWrapper(krakenApi);
	CurrencyPairDao currencyPairDao = new CurrencyPairDao(api);
	TradeDao dao = new TradeDao(api);
	TradeUcImpl uc = new TradeUcImpl(currencyPairDao, dao);

	currencyPairDao.getLotSize(new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC));

	Order o = new Order();
	o.setAmount(0.001);
	o.setPair(new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC));
	o.setType(OrderType.SELL);

	uc.perform(o);
    }

}
