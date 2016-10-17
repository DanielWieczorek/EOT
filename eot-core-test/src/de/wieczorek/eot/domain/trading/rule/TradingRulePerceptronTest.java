package de.wieczorek.eot.domain.trading.rule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import static org.assertj.core.api.Assertions.*;

import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;

@RunWith(JUnitPlatform.class)
public class TradingRulePerceptronTest {

    @DisplayName("First Test")
    @Test
    void testCombine() {
	TradingRule t1 = new TradingRule();
	t1.setComparator(ComparatorType.GREATER);
	t1.setThreshold(50);
	t1.setMetric(new RsiGraphMetric());

	TradingRule t2 = new TradingRule();
	t1.setComparator(ComparatorType.GREATER);
	t1.setThreshold(10);
	t1.setMetric(new RsiGraphMetric());
	TradingRulePerceptron p1 = new TradingRulePerceptron(t1, 1, 1);

	TradingRulePerceptron p2 = new TradingRulePerceptron(t2, 1, 1);
	
	TradingRulePerceptron result = p1.combineWith(p2);
	
	assertThat(result).extracting("inputs").containsExactly(new TradingRulePerceptron.)
    }
}
