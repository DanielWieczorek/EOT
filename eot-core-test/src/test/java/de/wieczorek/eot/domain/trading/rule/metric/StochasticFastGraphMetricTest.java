package de.wieczorek.eot.domain.trading.rule.metric;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

@RunWith(JUnitPlatform.class)
public class StochasticFastGraphMetricTest {

    @Test
    public void testAverage() {
	StochasticFastGraphMetric fast = new StochasticFastGraphMetric();
	ExchangeRateHistory history = new ExchangeRateHistory();
	history.add(new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now()));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(1)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(2)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 5, LocalDateTime.now().plusMinutes(3)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(4)));

	double result = fast.getRating(history);

	assertThat(result).isEqualTo(50);
    }

    @Test
    public void testConstant() {
	StochasticFastGraphMetric fast = new StochasticFastGraphMetric();
	ExchangeRateHistory history = new ExchangeRateHistory();
	history.add(new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now()));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now().plusMinutes(1)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now().plusMinutes(2)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now().plusMinutes(3)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now().plusMinutes(4)));

	double result = fast.getRating(history);

	assertThat(result).isEqualTo(Double.NaN);
    }

    @Test
    public void testFalling() {
	StochasticFastGraphMetric fast = new StochasticFastGraphMetric();
	ExchangeRateHistory history = new ExchangeRateHistory();
	history.add(new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 5, LocalDateTime.now()));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(1)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(2)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(3)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now().plusMinutes(4)));

	double result = fast.getRating(history);

	assertThat(result).isEqualTo(0);
    }

    @Test
    public void testRising() {
	StochasticFastGraphMetric fast = new StochasticFastGraphMetric();
	ExchangeRateHistory history = new ExchangeRateHistory();
	history.add(new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 1, LocalDateTime.now()));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(1)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(2)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 3, LocalDateTime.now().plusMinutes(3)));
	history.add(
		new TimedExchangeRate(ExchangableType.ETH, ExchangableType.BTC, 5, LocalDateTime.now().plusMinutes(4)));

	double result = fast.getRating(history);

	assertThat(result).isEqualTo(100);
    }
}
