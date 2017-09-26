package de.wieczorek.eot.domain.trading.rule.comparator;

import java.util.List;

public interface ITradingRuleComparator {
    boolean apply(double value);

    ITradingRuleComparator combineWith(List<ITradingRuleComparator> comparators);

    String printDescription();
}
