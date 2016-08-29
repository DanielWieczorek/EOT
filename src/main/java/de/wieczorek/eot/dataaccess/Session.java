package de.wieczorek.eot.dataaccess;

public class Session {
	  private final String username;
	  private final String apiKey;
	  private final String apiSecret;
	  private int nonce;
	  private static Session sessionInstance;
	private Session(String username, String apiKey, String apiSecret) {
		super();
		this.username = username;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.setNonce(Integer.valueOf((int) (System.currentTimeMillis() / 1000)));
	}
	public static Session create(String username, String apiKey, String apiSecret){
		if(!isActive())
			sessionInstance = new Session(username, apiKey, apiSecret);
		return sessionInstance;
		
	}
	
	public static Session getInstance(){
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

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public String getApiSecret() {
		return apiSecret;
	}
}
