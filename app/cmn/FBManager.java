package cmn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;
import play.Play;
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
	
	public static final boolean IS_MAGIC_MODE = Boolean.parseBoolean(Play.application().configuration().getString("magic"));
	
	private static final SimpleDateFormat fbDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private FBManager() {
		
	}
	
	public static FBManager getInstance() {
		return instance;
	}
	
	public boolean hasBirthdayOccured(String birthDate) {
		try {
			Date userBirthDate = fbDateFormat.parse(birthDate);
    		Calendar calendar = Calendar.getInstance();
    		int curYear = calendar.get(Calendar.YEAR);
    		calendar.setTime(userBirthDate);
    		calendar.set(Calendar.YEAR, curYear);
    		
    		Logger.debug("Calculated time = " + calendar.getTime());
    		
    		if(Calendar.getInstance().getTime().after(calendar.getTime()))
				return true;
		} catch (ParseException e) {
			Logger.error("Failed to parse user birth date = " + birthDate, e);
			return false;
		}
		return false;
	}
	
	public UserProfile fetchUserProfile(String uid) {
		FacebookClient facebookClient = new DefaultFacebookClient(tokenCache.getToken(uid));
		User user = facebookClient.fetchObject("me", User.class);
		UserProfile profile = new UserProfile(user.getId(), 
				user.getName() == null ? user.getFirstName() : user.getName(), user.getBirthday());
		return profile;
	}
	
	public int wishThemBack(String uid, int likeCount, int sayThanksAlotCount, int sayThankYouCount, List<String> postIdList) {
		FacebookClient facebookClient = new DefaultFacebookClient(tokenCache.getToken(uid));
		
		List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
		
		for(int i=0; i<likeCount; i++) {
			BatchRequest request = new BatchRequestBuilder(postIdList.get(i) + "/likes").method("POST").build();
			batchRequests.add(request);
		}
		
		for(int i=0; i < sayThanksAlotCount; i++) {
			BatchRequest request = new BatchRequestBuilder(postIdList.get(i + likeCount) + "/comments")
	  			.method("POST").parameters(Parameter.with("message", "Thanks a lot!")).build();
			batchRequests.add(request);
		}
		
		for(int i=0; i < sayThankYouCount; i++) {
			BatchRequest request = new BatchRequestBuilder(postIdList.get(i + likeCount + sayThanksAlotCount) + "/comments")
	  			.method("POST").parameters(Parameter.with("message", "Thanks you!")).build();
				batchRequests.add(request);
		}
		
		BatchRequest request = new BatchRequestBuilder(uid + "/permissions")
			.method("DELETE").build();
	
		batchRequests.add(request);
		
		int count = postIdList.size();
		List<BinaryAttachment> list = new ArrayList<BinaryAttachment>();
    	List<BatchResponse> batchResponses =
    			  facebookClient.executeBatch(batchRequests, list);
    	int counter = 1;
    	for(BatchResponse response : batchResponses) {
    		if(response.getCode() != 200) {
    			count--;
    			Logger.error("Failed to execute request no " + counter + " in a batch => " + response);
    		}
    		counter++;
    	}
    	
    	return count;
	}
	
	public PostDataWrapper findNonBirthdayPosts(String uid) {
		PostDataWrapper wrapper = new PostDataWrapper();
		List<BdayPost> notMatchedPostList = new ArrayList<BdayPost>();
		List<BdayPost> matchedPostList = new ArrayList<BdayPost>();
		Date filterDate = null;
    	Date beforeBirthDate = null;
    	
		FacebookClient facebookClient = new DefaultFacebookClient(tokenCache.getToken(uid));
		
		User user = facebookClient.fetchObject("me", User.class);
		
    	try {
    		Date userBirthDate = fbDateFormat.parse(user.getBirthday());
    		Logger.debug("User birthdate = " + userBirthDate);
    		Calendar calendar = Calendar.getInstance();
    		int curYear = calendar.get(Calendar.YEAR);
    		calendar.setTime(userBirthDate);
    		if(IS_MAGIC_MODE)
    			calendar.set(Calendar.YEAR, curYear - 1);
    		else
    			calendar.set(Calendar.YEAR, curYear);
    		filterDate = new Date(calendar.getTimeInMillis() + 1000L * 60L * 60L * 24L);
    		beforeBirthDate = new Date(calendar.getTimeInMillis() - 1000L * 60L * 60L * 24L);
		} catch (ParseException e) {
			Logger.error("Failed to parse birth date", e);
		}
    	
    	Logger.debug("Using filter date = " + filterDate + " and end date = " + beforeBirthDate
    			+ " for user " + user.getName() + ", Birthdate = " + user.getBirthday());
    	
    	Connection<Post> myFeed =
  			  facebookClient.fetchConnection("me/feed", Post.class,
  					  Parameter.with("until", filterDate),
  					  Parameter.with("limit", "50"));
    	
    	StringBuilder patternBuilder = new StringBuilder();
    	patternBuilder.append("\\b(?:happy|birthday|bday|B'day|wish");
    	if(user.getFirstName() != null)
    		patternBuilder.append("|").append(user.getFirstName());
    	if(user.getLastName() != null)
    		patternBuilder.append("|").append(user.getLastName());
    	if(user.getName() != null)
    		patternBuilder.append("|").append(user.getName());
    	patternBuilder.append(")\\b");
    	
    	Pattern pattern = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    	
    	int total = 0;
    	
    	boolean stopNow = false;
    	for (List<Post> myFeedConnectionPage : myFeed) {
    		  for (Post post : myFeedConnectionPage) {
    			  Date createdTimeDate = post.getCreatedTime();
    			  if(createdTimeDate.after(beforeBirthDate)) {
    				  String message = post.getMessage();
    				  if(message != null) {
    					  Matcher matcher = pattern.matcher(message);
    					  if(matcher.find()) {
    						  Logger.debug(post.getId() + " Message (" + message + ") from " + post.getFrom().getName() + " matches");
    						  BdayPost matchedPost = new BdayPost();
    						  matchedPost.friendName = post.getFrom().getName();
    						  matchedPost.postId = post.getId();
    						  matchedPost.postData = message;
    						  matched++;
    						  matchedPostList.add(matchedPost);
    					  } else {
    						  Logger.debug("Message (" + message + ") " + post.getFrom().getName() + " does not match!");
    						  BdayPost notMatchedPost = new BdayPost();
    						  notMatchedPost.friendName = post.getFrom().getName();
    						  notMatchedPost.postId = post.getId();
    						  notMatchedPost.postData = message;
    						  ProfilePic frndPic = facebookClient.fetchObject(post.getFrom().getId() + "/picture", ProfilePic.class, 
    		    		    			Parameter.with("redirect", "false"));
    						  notMatchedPost.profilPicUrl = frndPic.data.url;
    						  Logger.debug("Not matched post = " + notMatchedPost);
    						  notMatchedPostList.add(notMatchedPost);
    					  }
    				  }
    			  } else {
    				  total = matchedPostList.size() + notMatchedPostList.size();
    				  Logger.debug("Total = " + total + ", Matched = " 
    						  + matchedPostList.size() + ", Not matched = " + notMatchedPostList.size());
    				  Logger.debug("Next Post: " + post);
    				  stopNow = true;
    				  break;
    			  }
    		  }
    		  if(stopNow)
    			  break;
    	}
    	
    	wrapper.setNotMatched(notMatchedPostList);
    	wrapper.setNotMatchedCount(notMatchedPostList.size());
    	wrapper.setMatched(matchedPostList);
    	wrapper.setMatchedCount(matchedPostList.size());
    	wrapper.setTotalCount(total);
    	wrapper.setBdayString(user.getBirthday());
		
		return wrapper;
		
	}

}
