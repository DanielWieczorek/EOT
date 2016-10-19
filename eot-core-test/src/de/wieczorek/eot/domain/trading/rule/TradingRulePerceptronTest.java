package de.wieczorek.eot.domain.trading.rule;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import de.wieczorek.eot.domain.trading.rule.metric.CoppocGraphMetric;
import de.wieczorek.eot.domain.trading.rule.metric.RsiGraphMetric;

@RunWith(JUnitPlatform.class)
public class TradingRulePerceptronTest {

    @Test
    void testCombine() {
	TradingRule t1 = new TradingRule();
	t1.setComparator(ComparatorType.GREATER);
	t1.setThreshold(50);
	t1.setMetric(new RsiGraphMetric());

	TradingRule t2 = new TradingRule();
	t2.setComparator(ComparatorType.GREATER);
	t2.setThreshold(10);
	t2.setMetric(new RsiGraphMetric());
	TradingRulePerceptron p1 = new TradingRulePerceptron(t1, 1, 1);

	TradingRulePerceptron p2 = new TradingRulePerceptron(t2, 1, 1);

	TradingRulePerceptron result = p1.combineWith(p2);

	assertThat(result.getInputs()).allMatch(item -> item.getRule().getThreshold() == 30)
		.allMatch(item -> item.getWeight() == 2)
		.allMatch(item -> item.getRule().getComparator() == ComparatorType.GREATER)
		.allMatch(item -> item.getRule().getComparator() == ComparatorType.GREATER);
	assertThat(result.getInputs().size()).isEqualTo(1);
    }

    @Test
    void testCombineDifferentComparator() {
	TradingRule t1 = new TradingRule();
	t1.setComparator(ComparatorType.LESS);
	t1.setThreshold(50);
	t1.setMetric(new RsiGraphMetric());

	TradingRule t2 = new TradingRule();
	t2.setComparator(ComparatorType.GREATER);
	t2.setThreshold(10);
	t2.setMetric(new RsiGraphMetric());
	TradingRulePerceptron p1 = new TradingRulePerceptron(t1, 1, 1);

	TradingRulePerceptron p2 = new TradingRulePerceptron(t2, 1, 1);

	TradingRulePerceptron result = p1.combineWith(p2);

	assertThat(result.getInputs().get(0)).matches(item -> item.getRule().getThreshold() == t1.getThreshold())
		.matches(item -> item.getRule().getComparator() == t1.getComparator());
	assertThat(result.getInputs().get(1)).matches(item -> item.getRule().getThreshold() == t2.getThreshold())
		.matches(item -> item.getRule().getComparator() == t2.getComparator());
	assertThat(result.getInputs().size()).isEqualTo(2);
	assertThat(result.getThreshold()).isEqualTo(p1.getThreshold() + p2.getThreshold());
    }

    @Test
    void testCombineTwoInputs() {
	TradingRule t1 = new TradingRule();
	t1.setComparator(ComparatorType.GREATER);
	t1.setThreshold(50);
	t1.setMetric(new RsiGraphMetric());

	TradingRule t3 = new TradingRule();
	t3.setComparator(ComparatorType.GREATER);
	t3.setThreshold(50);
	t3.setMetric(new CoppocGraphMetric());

	TradingRule t2 = new TradingRule();
	t2.setComparator(ComparatorType.GREATER);
	t2.setThreshold(10);
	t2.setMetric(new RsiGraphMetric());
	TradingRulePerceptron p1 = new TradingRulePerceptron(t1, 1, 2);
	p1.add(t3, 1);

	TradingRulePerceptron p2 = new TradingRulePerceptron(t2, 1, 1);

	TradingRulePerceptron result = p1.combineWith(p2);

	assertThat(result.getInputs().get(1)).matches(item -> item.getRule().getThreshold() == 30)
		.matches(item -> item.getWeight() == 2)
		.matches(item -> item.getRule().getComparator() == ComparatorType.GREATER)
		.matches(item -> item.getRule().getComparator() == ComparatorType.GREATER);
	assertThat(result.getInputs().get(0)).matches(item -> item.getRule().getThreshold() == t3.getThreshold())
		.matches(item -> item.getRule().getComparator() == t3.getComparator());
	assertThat(result.getInputs().size()).isEqualTo(2);
	assertThat(result.getThreshold()).isEqualTo(p1.getThreshold() + p2.getThreshold());
    }
}
