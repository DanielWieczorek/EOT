package de.wieczorek.eot.domain.trading.rule;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public interface INeuralNetworkNode {

    boolean isActivated(final ExchangeRateHistory history);
}
