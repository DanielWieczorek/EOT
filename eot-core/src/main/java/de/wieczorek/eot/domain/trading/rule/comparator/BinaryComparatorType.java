package de.wieczorek.eot.domain.trading.rule.comparator;

import de.wieczorek.eot.domain.trading.rule.TradingRule;
import de.wieczorek.eot.domain.trading.rule.metric.AbstractGraphMetric;

/**
 * Comparator types for the {@link TradingRule}. The trading rule triggers if
 * value computed by the {@link AbstractGraphMetric} is lower, greater or equal
 * to a certain value.
 * 
 * @author Daniel Wieczorek
 *
 */
public enum BinaryComparatorType {
    /**
     * The computed value has to be greater than a given value.
     */
    GREATER,
    /**
     * The computed value has to be lower than to a given value.
     */
    LESS
}
