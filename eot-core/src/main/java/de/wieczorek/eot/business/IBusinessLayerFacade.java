package de.wieczorek.eot.business;

import de.wieczorek.eot.business.configuration.IConfigurationUc;
import de.wieczorek.eot.business.history.IChartHistoryUc;
import de.wieczorek.eot.business.price.IExchangeRateUc;

public interface IBusinessLayerFacade extends IChartHistoryUc, IExchangeRateUc, IConfigurationUc {

}
