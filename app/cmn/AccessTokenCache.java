package cmn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccessTokenCache {
	
	private static final AccessTokenCache instance = new AccessTokenCache();
	
	private static final Map<String, String> tokenMap = new ConcurrentHashMap<String, String>();
	
	private AccessTokenCache() {
		
	}
	
	public static final AccessTokenCache getInstance() {
		return instance;
	}
	
	public void addToken(String uid, String accessToken) {
		tokenMap.put(uid, accessToken);
	}
	
	public String getToken(String uid) {
		return tokenMap.get(uid);
	}

}
