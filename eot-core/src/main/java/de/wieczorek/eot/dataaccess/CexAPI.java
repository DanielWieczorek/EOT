/**
 * This project is licensed under the terms of the MIT license, you can read more in LICENSE.txt.
 *
 * CexAPI.java
 *
 * <pre>
 * @version 2.0.0
 * @author  Zack Urben
 * @contact zackurben@gmail.com
 *
 * @support
 * Motivation BTC    @ 1HvXfXRP9gZqHPkQUCPKmt5wKyXDMADhvQ
 * Cryptsy Trade Key @ e5447842f0b6605ad45ced133b4cdd5135a4838c
 * </pre>
 *
 * This script requires a free API Key from Cex.io, which can be obtained here:
 * https://cex.io/trade/profile
 *
 * This API Key requires the following permissions:
 * Account Balance, Place Order, Cancel Order, Open Order
 */

package de.wieczorek.eot.dataaccess;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import de.wieczorek.eot.domain.exchangable.ExchangableType;

/**
 * Class for accessing the web services of cex.io.
 *
 * @author Daniel Wieczorek
 *
 */
public class CexAPI {

    /**
     * Session used for authentication.
     */
    private final Session session;

    /**
     * Creates a CexAPI Object.
     *
     * @param sessionInput
     *            session needed for autentication
     */
    public CexAPI(final Session sessionInput) {

	this.session = sessionInput;
    }

    /**
     * Debug the contents of the a CexAPI Object.
     *
     * @return The CexAPI object data: username, apiKey, apiSecret, and nonce.
     */
    @Override
    public final String toString() {
	return "{\"username\":\"" + session.getUsername() + "\",\"apiKey\":\"" + session.getApiKey()
		+ "\",\"apiSecret\":\"" + session.getApiSecret() + "\",\"nonce\":\"" + session.getNonce() + "\"}";
    }

    /**
     * Create HMAC-SHA256 signature for our POST call.
     *
     * @return HMAC-SHA256 message for POST authentication.
     */
    private String signature() {
	session.setNonce(session.getNonce() + 1);
	final String message = new String(session.getNonce() + session.getUsername() + session.getApiKey());
	Mac hmac = null;

	try {
	    hmac = Mac.getInstance("HmacSHA256");
	    final SecretKeySpec secretKey = new SecretKeySpec(session.getApiSecret().getBytes("UTF-8"), "HmacSHA256");
	    hmac.init(secretKey);
	} catch (final NoSuchAlgorithmException e) {
	    e.printStackTrace();
	} catch (final InvalidKeyException e) {
	    e.printStackTrace();
	} catch (final UnsupportedEncodingException e) {
	    e.printStackTrace();
	}

	return String.format("%X", new BigInteger(1, hmac.doFinal(message.getBytes())));
    }

    /**
     * Make a POST request to the Cex.io API, with the given data.
     *
     * @param addr
     *            HTTP Address to make the request.
     * @param param
     *            Parameters to add to the POST request.
     * @param auth
     *            Authentication required flag.
     *
     * @return Result from POST sent to server.
     */
    private String post(final String addr, final String param, final boolean auth) {
	boolean sent = false;
	String response = "";

	while (!sent) {
	    sent = true;
	    HttpURLConnection connection = null;
	    DataOutputStream output = null;
	    BufferedReader input = null;
	    final String charset = "UTF-8";

	    try {
		connection = (HttpURLConnection) new URL(addr).openConnection();
		connection.setRequestProperty("User-Agent", "Cex.io Java API");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Charset", charset);

		// Add parameters if included with the call or authorization is
		// required.
		if (param != "" || auth) {
		    String content = "";
		    connection.setDoOutput(true);
		    output = new DataOutputStream(connection.getOutputStream());

		    // Add authorization details if required for the API method.
		    if (auth) {
			// Generate POST variables and catch errors.
			final String tSig = this.signature();
			final String tNon = String.valueOf(session.getNonce());

			content = "key=" + URLEncoder.encode(session.getApiKey(), charset) + "&signature="
				+ URLEncoder.encode(tSig, charset) + "&nonce=" + URLEncoder.encode(tNon, charset);
		    }

		    // Separate parameters and add them to the request URL.
		    if (param.contains(",")) {
			final String[] temp = param.split(",");

			for (int a = 0; a < temp.length; a += 2) {
			    content += "&" + temp[a] + "=" + temp[a + 1] + "&";
			}

			content = content.substring(0, content.length() - 1);
		    }

		    output.writeBytes(content);
		    output.flush();
		    output.close();
		}

		String temp = "";
		input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		while ((temp = input.readLine()) != null) {
		    response += temp;
		}

		input.close();
	    } catch (final MalformedURLException e) {
		sent = false;
		e.printStackTrace();
	    } catch (final IOException e) {
		sent = false;
		e.printStackTrace();

		// This will trigger if CloudFlare is active (Cex API is down).
		final int maxWaitTime = 10000;
		try {
		    Thread.sleep(maxWaitTime);
		} catch (final InterruptedException ie) {
		    ie.printStackTrace();
		}
	    }
	}

	return response;
    }

    /**
     * Wrapper for post method; builds the correct URL for the POST request.
     *
     * @param method
     *            Method for the POST request.
     * @param pair
     *            Cex.io currency pair for the POST request.
     * @param param
     *            Parameters to add to the POST request.
     * @param auth
     *            Authentication required flag.
     *
     * @return Result from POST sent to server.
     */
    private String apiCall(final String method, final String pair, final String param, final boolean auth) {
	return this.post(("https://cex.io/api/" + method + "/" + pair), param, auth);
    }

    /**
     * Fetch the ticker data, for the given currency pair.
     *
     * @param pair
     *            Cex.io currency pair for the POST request.
     *
     * @return The public ticker data for the given pair.
     */
    public final String ticker(final String pair) {
	return this.apiCall("ticker", pair, "", false);
    }

    /**
     * Fetch the last price for the given currency pairs.
     *
     * @param major
     *            Cex.io major currency pair.
     * @param minor
     *            Cex.io minor currency pair.
     *
     * @return The last trade price for the given currency pairs.
     */
    public final String lastPrice(final ExchangableType major, final ExchangableType minor) {
	return this.apiCall("last_price", (major + "/" + minor), "", false);
    }

    /**
     * Fetch the price conversion from the Major to Minor currency.
     *
     * @param major
     *            Cex.io major currency.
     * @param minor
     *            Cex.io minor currency.
     * @param amount
     *            amount of the major currency to convert
     * @return The the value of the minor currency in relation to the major
     *         currency.
     */
    public final String convert(final String major, final String minor, final float amount) {
	return this.apiCall("convert", (major + "/" + minor), ("amnt," + amount), false);
    }

    /**
     * Fetch the historical data points for the Major to Minor currency.
     *
     * @param major
     *            Cex.io major currency pair.
     * @param minor
     *            Cex.io minor currency pair.
     * @param hours
     *            The past-tense number of hours to pull data from.
     * @param count
     *            The maximum number of results desired.
     *
     * @return historical data points for the Major to Minor currency.
     */
    public final String chart(final ExchangableType major, final ExchangableType minor, final int hours,
	    final int count) {
	return this.apiCall("price_stats", (major + "/" + minor), ("lastHours," + hours + ",maxRespArrSize," + count),
		false);
    }

    /**
     * Fetch the order book data, for the given currency pair.
     *
     * @param pair
     *            Cex.io currency pair for the POST request.
     *
     * @return The public order book data for the given pair.
     */
    public final String orderBook(final String pair) {
	return this.apiCall("order_book", pair, "", false);
    }

    /**
     * Fetch the trade history data, for the given currency pair.
     *
     * @param pair
     *            Cex.io currency pair for the POST request.
     * @param since
     *            Unix time stamp to retrieve the data from.
     *
     * @return The public trade history for the given pair (Currently limited to
     *         the last 1000 trades).
     */
    public final String tradeHistory(final String pair, final int since) {
	return this.apiCall("trade_history", pair, ("since," + since), false);
    }

    /**
     * Fetch the account balance data, for the Cex.io API Object.
     *
     * @return The account balance for all currency pairs.
     */
    public final String balance() {
	return this.apiCall("balance", "", "", true);
    }

    /**
     * Fetch the accounts open orders, for the given currency pair.
     *
     * @param pair
     *            Cex.io currency pair for the POST request.
     *
     * @return The account open orders for the currency pair.
     */
    public final String openOrders(final String pair) {
	return this.apiCall("open_orders", pair, "", true);
    }

    /**
     * Cancel the account order, with the given ID.
     *
     * @param id
     *            The order ID number
     *
     * @return The boolean successfulness of the order cancellation:
     *         (True/False).
     */
    public final String cancelOrder(final String id) {
	return this.apiCall("cancel_order", "", ("id," + id), true);
    }

    /**
     * Place an order, via the Cex.io API, for the given currency pair, with the
     * given amount and price.
     *
     * @param pair
     *            Cex.io currency pair for the POST request.
     * @param type
     *            Order type (buy/sell).
     * @param amount
     *            Order amount.
     * @param price
     *            Order price.
     *
     * @return The order information, including: the order id, time, pending,
     *         amount, type, and price.
     */
    public final String placeOrder(final String pair, final String type, final float amount, final float price) {
	return this.apiCall("place_order", pair, ("type," + type + ",amount," + amount + ",price," + price), true);
    }

    /**
     * Get the GHash hash rates.
     *
     * @return The hash rates for the past day, with various time metrics.
     */
    public final String hashrate() {
	return this.apiCall("ghash.io", "hashrate", "", true);
    }

    /**
     * Get the GHash hash rate for each worker.
     *
     * @return The hash rates for each worker for the past day, with various
     *         time metrics.
     */
    public final String workers() {
	return this.apiCall("ghash.io", "workers", "", true);
    }

    /**
     * Retrieves the detailed (1 minute precision) chart data as ohclv.
     *
     * @param major
     *            source currency
     * @param minor
     *            target currency
     * @param timestamp
     *            start time
     * @return a JSON string containing the data
     */
    public final String ohclv(final ExchangableType major, final ExchangableType minor, final String timestamp) {
	return this.apiCall("ohlcv/hd/" + timestamp, (major + "/" + minor), "", false);
    }
}
