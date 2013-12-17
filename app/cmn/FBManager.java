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
	
	private static final SimpleDateFormat fbDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private FBManager() {
		
	}
	
	public FBManager getInstance() {
		return instance;
	}
	
	public List<BdayPost> findNonBirthdayPosts(String uid) {
		List<BdayPost> bdayPostList = new ArrayList<BdayPost>();
		Date filterDate = null;
    	Date beforeBirthDate = null;
    	
		FacebookClient facebookClient = new DefaultFacebookClient(tokenCache.getToken(uid));
		
		User user = facebookClient.fetchObject("me", User.class);
		
    	try {
    		Date userBirthDate = fbDateFormat.parse(user.getBirthday());
    		System.out.println("User birthdate = " + userBirthDate);
    		Calendar calendar = Calendar.getInstance();
    		int curYear = calendar.get(Calendar.YEAR);
    		calendar.setTime(userBirthDate);
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
    	patternBuilder.append("\\b(?:happy|birthday|bday|B'day");
    	if(user.getFirstName() != null)
    		patternBuilder.append("|").append(user.getFirstName());
    	if(user.getLastName() != null)
    		patternBuilder.append("|").append(user.getLastName());
    	if(user.getName() != null)
    		patternBuilder.append("|").append(user.getName());
    	patternBuilder.append(")\\b");
    	
    	Pattern pattern = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    	
    	int total = 0;
    	int matched = 0;
    	int notMatched = 0;
    	
    	boolean stopNow = false;
    	for (List<Post> myFeedConnectionPage : myFeed) {
    		  for (Post post : myFeedConnectionPage) {
    			  Date createdTimeDate = post.getCreatedTime();
    			  if(createdTimeDate.after(beforeBirthDate)) {
    				  String message = post.getMessage();
    				  if(message != null) {
    					  Matcher matcher = pattern.matcher(message);
    					  if(matcher.find()) {
    						  Logger.debug(post.getId() + " Message (" + message + ") matches");
    						  matched++;
    					  } else {
    						  Logger.debug("Message (" + message + ") does not match!");
    						  BdayPost notMatchedPost = new BdayPost();
    					  }
    				  }
    				  total++;
    			  } else {
    				  notMatched = total - matched;
    				  Logger.debug("Total = " + total + ", Matched = " 
    						  + matched + ", Not matched = " + notMatched);
    				  Logger.debug("Next Post: " + post);
    				  stopNow = true;
    				  break;
    			  }
    		  }
    		  if(stopNow)
    			  break;
    	}
		
		return bdayPostList;
		
	}

}
