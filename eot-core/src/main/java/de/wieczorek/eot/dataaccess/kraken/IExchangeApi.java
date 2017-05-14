package de.wieczorek.eot.dataaccess.kraken;

import java.io.IOException;

import de.wieczorek.eot.domain.exchangable.ExchangableType;

public interface IExchangeApi {
    String lastPrice(final ExchangableType major, final ExchangableType minor) throws IOException;

    String ohclv(final ExchangableType major, final ExchangableType minor, final long timestamp) throws IOException;
}
