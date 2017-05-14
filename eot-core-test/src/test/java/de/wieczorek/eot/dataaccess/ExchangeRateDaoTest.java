package de.wieczorek.eot.dataaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import de.wieczorek.eot.business.bo.ExchangeRateBo;
import de.wieczorek.eot.business.bo.ExchangeRateBoKey;
import de.wieczorek.eot.dataaccess.kraken.IExchangeApi;
import de.wieczorek.eot.domain.exchangable.ExchangableType;
import de.wieczorek.eot.domain.exchangable.rate.TimedExchangeRate;

@RunWith(JUnitPlatform.class)
public class ExchangeRateDaoTest {

    @Test
    public void getDetailedHistoryTest() throws IOException, JSONException {
	IExchangeApi api = mock(IExchangeApi.class);
	ExchangeRateDao dao = new ExchangeRateDao(api);

	when(api.ohclv(eq(ExchangableType.ETH), eq(ExchangableType.BTC), anyLong())).thenReturn(
		"{\"error\":[],\"result\":{\"XETHXXBT\":[[1493299440,\"0.039820\",\"0.039920\",\"0.039810\",\"0.039849\",\"0.039881\",\"91.19489066\",14]"
			+ ",[1493299500,\"0.045563\",\"0.045564\",\"0.045347\",\"0.045347\",\"0.045449\",\"429.15514646\",57]],\"last\":1493299560}}");

	ExchangeRateBo first = new ExchangeRateBo();
	first.setKey(new ExchangeRateBoKey(1493299440, ExchangableType.ETH, ExchangableType.BTC));
	first.setExchangeRate(0.039849);

	ExchangeRateBo second = new ExchangeRateBo();
	second.setKey(new ExchangeRateBoKey(1493299500, ExchangableType.ETH, ExchangableType.BTC));
	second.setExchangeRate(0.045347);

	List<ExchangeRateBo> result = dao.getDetailedHistoryEntries(ExchangableType.ETH, ExchangableType.BTC, 1);
	assertThat(result).containsOnly(first, second);
    }

    @Test
    public void getLastPriceTest() throws IOException, JSONException {
	IExchangeApi api = mock(IExchangeApi.class);
	ExchangeRateDao dao = new ExchangeRateDao(api);

	when(api.lastPrice(eq(ExchangableType.ETH), eq(ExchangableType.BTC))).thenReturn(
		"{\"error\":[],\"result\":{\"XETHXXBT\":{\"a\":[\"0.052967\",\"42\",\"42.000\"],\"b\":[\"0.052891\",\"10\",\"10.000\"],\"c\":[\"0.052993\",\"0.04235031\"],\"v\":[\"237072.97290145\",\"252424.24133326\"],\"p\":[\"0.049988\",\"0.049746\"],\"t\":[15098,16035],\"l\":[\"0.046890\",\"0.046401\"],\"h\":[\"0.058500\",\"0.058500\"],\"o\":\"0.047199\"}}}");

	TimedExchangeRate result = dao.getCurrentExchangeRate(ExchangableType.ETH, ExchangableType.BTC);
	assertThat(result.getToPrice()).isEqualTo(0.052993);
	assertThat(result.getFrom()).isEqualTo(ExchangableType.ETH);
	assertThat(result.getTo()).isEqualTo(ExchangableType.BTC);
    }
}