package de.wieczorek.eot.dataaccess.kraken;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import de.wieczorek.eot.business.trade.impl.OrderBo;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

/**
 * Universal API which has to be implemented by all Exchange specific
 * implementations.
 * 
 * @author Daniel Wieczorek
 *
 */
public interface IExchangeApi {
    /**
     * Requests the last price for a pair of currency.
     * 
     * @param major
     *            major currency of the pair
     * @param minor
     *            minor currency of the pair
     * @return the JSON result last price
     * @throws IOException
     */
    String lastPrice(final ExchangableType major, final ExchangableType minor) throws IOException;

    String ohclv(final ExchangableType major, final ExchangableType minor, final long timestamp) throws IOException;

    String getAccountBalance() throws InvalidKeyException, NoSuchAlgorithmException, IOException;

    String getAssetPairInfo(ExchangablePair pair) throws IOException;

    String performOrder(OrderBo order) throws IOException, InvalidKeyException, NoSuchAlgorithmException;
}
