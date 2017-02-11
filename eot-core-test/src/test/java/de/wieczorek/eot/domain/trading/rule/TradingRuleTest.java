package de.wieczorek.eot.domain.trading.rule;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;
import de.wieczorek.eot.domain.trading.rule.metric.StochasticFastGraphMetric;

@RunWith(JUnitPlatform.class)
public class TradingRuleTest {

    @Test
    public void nanTest() {
	StochasticFastGraphMetric fast = new StochasticFastGraphMetric();
	ExchangeRateHistory history = new ExchangeRateHistory();

	TradingRule rule = new TradingRule();
	rule.setComparator(ComparatorType.EQUAL);
	rule.setMetric(fast);
	rule.setThreshold(1);

	history.add(new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 5, LocalDateTime.now()));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(1)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(2)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(3)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now().plusMinutes(4)));

	boolean result = rule.evaluate(history);

	assertThat(result).isEqualTo(false);

    }
}
