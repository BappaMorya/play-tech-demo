package cmn;

import java.util.concurrent.TimeUnit;

import play.Logger;
import play.Play;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class AccessTokenCache {
	
	private static final AccessTokenCache instance = new AccessTokenCache();
	
	private static Cache<String, String> tokenCache = null;
	
	private AccessTokenCache() {
		long cacheTimeout = 0L;
		try {
			cacheTimeout = Long.parseLong(Play.application().configuration().getString("cacheTimeout"));
		} catch (NumberFormatException nfe) {
			cacheTimeout = 10;
		}
		
		RemovalListener<String, String> listener = new RemovalListener<String, String>() {
			
			@Override
			public void onRemoval(RemovalNotification<String, String> removal) {
				Logger.debug("Expiring Uid " + removal.getKey());
				UserPostStore.getInstance().removeUser(removal.getKey());
			}
		};
		
		tokenCache = CacheBuilder.newBuilder()
				.expireAfterWrite(cacheTimeout, TimeUnit.MINUTES)
				.removalListener(listener)
				.build();
	}
	
	public static final AccessTokenCache getInstance() {
		return instance;
	}
	
	public void addToken(String uid, String accessToken) {
		tokenCache.put(uid, accessToken);
	}
	
	public String getToken(String uid) {
		return tokenCache.getIfPresent(uid);
	}
	
	public void removeToken(String uid) {
		tokenCache.invalidate(uid);
	}

}
