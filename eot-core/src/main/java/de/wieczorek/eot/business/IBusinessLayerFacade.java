package de.wieczorek.eot.business;

import de.wieczorek.eot.business.configuration.IConfigurationUc;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;

/**
 * Facade to the to the Business layer. This is the only class which may be
 * Accessed from the Domain layer.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IBusinessLayerFacade extends IChartHistoryUc, IExchangeRateUc, IConfigurationUc {

}
