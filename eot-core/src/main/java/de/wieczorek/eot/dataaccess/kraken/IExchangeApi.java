package de.wieczorek.eot.dataaccess.kraken;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import de.wieczorek.eot.business.trade.impl.OrderBo;
import de.wieczorek.eot.domain.exchangable.ExchangablePair;
import de.wieczorek.eot.domain.exchangable.ExchangableType;

public interface IExchangeApi {
    String lastPrice(final ExchangableType major, final ExchangableType minor) throws IOException;

    String ohclv(final ExchangableType major, final ExchangableType minor, final long timestamp) throws IOException;

    String getAccountBalance() throws InvalidKeyException, NoSuchAlgorithmException, IOException;

    String getAssetPairInfo(ExchangablePair pair) throws IOException;

    String performOrder(OrderBo order) throws IOException, InvalidKeyException, NoSuchAlgorithmException;
}
