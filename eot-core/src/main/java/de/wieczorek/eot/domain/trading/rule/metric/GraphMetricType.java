package de.wieczorek.eot.domain.trading.rule.metric;

/**
 * Type of graph metric.
 * 
 * @author Daniel Wieczorek
 *
 */
public enum GraphMetricType {
    /**
     * RSI (Relative Strenght indicator) indicator.
     */
    RSI,
    /**
     * Coppoch momentum indicator.
     */
    Coppoch,

    StochasticFast,

    MACD,

    BollingerPercent,

    DiffToMax
}
