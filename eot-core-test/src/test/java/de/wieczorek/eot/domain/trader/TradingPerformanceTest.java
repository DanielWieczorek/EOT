package de.wieczorek.eot.domain.trader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableSet;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.Order;

@RunWith(JUnitPlatform.class)
public class TradingPerformanceTest {

    @Test
    public void testNetProfitCalculationNoChange() {
	IAccount account = new Account();
	account.deposit(new ExchangableSet(ExchangableType.BTC, 1.0));
	DummyExchange exchange = new DummyExchange();
	TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.BTC, 1.0));
	Trader t = new Trader("", account, exchange, null, null,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);

	exchange.currentExchangeRate = new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1.0, null);
	performance.update(t, null);

	assertThat(performance.getNetProfit()).isEqualTo(0.0);
    }

    @Test
    public void testNetProfitCalculationNoChangeOtherCurrency() {
	IAccount account = new Account();
	account.deposit(new ExchangableSet(ExchangableType.BTC, 1.0));
	DummyExchange exchange = new DummyExchange();
	TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.ETH, 1.0));
	Trader t = new Trader("", account, exchange, null, null,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);

	exchange.currentExchangeRate = new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1.0, null);
	performance.update(t, null);

	assertThat(performance.getNetProfit()).isEqualTo(0.0);
    }

    @Test
    public void testNetProfitCalculationAmountChangeOtherCurrency() {
	IAccount account = new Account();
	account.deposit(new ExchangableSet(ExchangableType.BTC, 0.5));
	DummyExchange exchange = new DummyExchange();
	TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.ETH, 1.0));
	Trader t = new Trader("", account, exchange, null, null,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);

	exchange.currentExchangeRate = new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1.0, null);
	performance.update(t, null);

	assertThat(performance.getNetProfit()).isEqualTo(-0.5);
    }

    @Test
    public void testNetProfitCalculationAmountAndRatioChangeOtherCurrency() {
	IAccount account = new Account();
	account.deposit(new ExchangableSet(ExchangableType.BTC, 0.5));
	DummyExchange exchange = new DummyExchange();
	TradingPerformance performance = new TradingPerformance(new ExchangableSet(ExchangableType.ETH, 1.0));
	Trader t = new Trader("", account, exchange, null, null,
		new ExchangablePair(ExchangableType.ETH, ExchangableType.BTC), performance);

	exchange.currentExchangeRate = new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 0.5, null);
	performance.update(t, null);

	assertThat(performance.getNetProfit()).isEqualTo(-0.75);
    }

    private class DummyExchange implements IExchange {

	private TimedExchangeRate currentExchangeRate;

	@Override
	public ExchangeRateHistory getExchangeRateHistory(ExchangablePair pair, int hours) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public TimedExchangeRate getCurrentExchangeRate(ExchangablePair pair) {

	    return currentExchangeRate;
	}

	@Override
	public ExchangableSet performOrder(Order o, Trader t) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public List<Order> getCurrentOrders(Trader trader) {
	    // TODO Auto-generated method stub
	    return null;
	}

    }
}
