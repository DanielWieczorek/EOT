package de.wieczorek.eot.business.trade.impl;

/**
 * Type of order.
 * 
 * @author Daniel Wieczorek
 *
 */
public enum OrderExecutionType {
    /**
     * Execute order at current market price.
     */
    market, //
    /**
     * Execute order at a certain price.
     */
    limit, //
    /**
     * Triggers a market order when the market price hits the stop price.
     */
    stopLoss, //
    /**
     * Triggers a market order (buy or sell) when market price hits the profit
     * price.
     */
    takeProfit, //
    /**
     * Either triggers a market order when market price hits the stop price or
     * triggers a market order when market price hits the profit price.
     */
    stopLossProfit, //
    /**
     * Either triggers a market order when market price hits the stop price or
     * fills a limit order at the limit price.
     */
    stopLossProfitLimit, //
    /**
     * Triggers a limit order when the market price hits the stop price.
     */
    stopLossLimit, //
    /**
     * Triggers a limit order (buy or sell) when market price hits the profit
     * price.
     */
    takeProfitLimit, //
    /**
     * Triggers a market order when market price goes against the position by
     * the stop offset amount.
     */
    trailingStop, //
    /**
     * Triggers a limit order when market price goes against the position by the
     * stop offset amount.
     */
    trailingStopLimit, //
    /**
     * Either triggers a market order when market price hits the stop price or
     * triggers a limit order when market price hits the profit price.
     */
    stopLossAndLimit, //
    /**
     * settles a short position.
     */
    settlePosition
}
