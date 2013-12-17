package cmn;

import java.util.List;

import models.BdayPost;

import com.restfb.BinaryAttachment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchRequest.BatchRequestBuilder;
import com.restfb.batch.BatchResponse;
import com.restfb.json.JsonObject;
import com.restfb.types.FacebookType;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FBManager {
	
	private static final FBManager instance = new FBManager();
	
	private static final AccessTokenCache tokenCache = AccessTokenCache.getInstance();
	
	private FBManager() {
		
	}
	
	public List<BdayPost> findNonBirthdayPosts(String uid) {
		
		return null;
		
	}

}
