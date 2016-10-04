package de.wieczorek.eot.dataaccess;

/**
 * Session object holding all information for the authentication towards the web
 * service from which the data is retrieved. service from where the data is
 * retrieved.
 *
 * @author Daniel Wieczorek
 *
 */
public final class Session {
    /**
     * The user as as which we execute the calls.
     */
    private final String username;
    /**
     * Unique key to belonging to the user.
     */
    private final String apiKey;
    /**
     * Secret used for authentication.
     */
    private final String apiSecret;
    /**
     * the nonce.
     */
    private int nonce;
    /**
     * Instance of the singleton of this class.
     */
    private static Session sessionInstance;

    /**
     * Constructor.
     *
     * @param usernameInput
     *            the user name
     * @param apiKeyInput
     *            the API key belonging to the user
     * @param apiSecretInput
     *            the corresponding secret
     */
    private Session(final String usernameInput, final String apiKeyInput, final String apiSecretInput) {
	super();
	this.username = usernameInput;
	this.apiKey = apiKeyInput;
	this.apiSecret = apiSecretInput;
	final int millisPerSecond = 1000;
	this.setNonce(Integer.valueOf((int) (System.currentTimeMillis() / millisPerSecond)));
    }

    /**
     * creates an instance of the Session or returns the session if it already
     * exists.
     *
     * @param username
     *            the user name
     * @param apiKey
     *            the API key belonging to the user
     * @param apiSecret
     *            the corresponding secret
     * @return either the created session or if already a session was created,
     *         then the existing session object
     */
    public static Session create(final String username, final String apiKey, final String apiSecret) {
	if (!isActive()) {
	    sessionInstance = new Session(username, apiKey, apiSecret);
	}
	return sessionInstance;

    }

    public static Session getInstance() {
	return sessionInstance;
    }

    public static boolean isActive() {
	return sessionInstance != null;
    }

    public String getUsername() {
	return username;
    }

    public String getApiKey() {
	return apiKey;
    }

    public int getNonce() {
	return nonce;
    }

    public void setNonce(final int nonceInput) {
	this.nonce = nonceInput;
    }

    public String getApiSecret() {
	return apiSecret;
    }
}
