package cmn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserPostStore {
	
	private static final UserPostStore instance = new UserPostStore();
	
	private Map<String, List<String>> matchedPostStore = new ConcurrentHashMap<String, List<String>>();
	
	private Map<String, List<String>> notMatchedPostStore = new ConcurrentHashMap<String, List<String>>();
	
	private UserPostStore() {
		
	}
	
	public static final UserPostStore getInstance() {
		return instance;
	}
	
	public void addMatchedPost(String uid, String postId) {
		List<String> postList = matchedPostStore.get(uid);
		if(postList == null) {
			postList = new ArrayList<String>();
			matchedPostStore.put(uid, postList);
		}
		postList.add(postId);
	}
	
	public void addNotMatchedPost(String uid, String postId) {
		List<String> postList = notMatchedPostStore.get(uid);
		if(postList == null) {
			postList = new ArrayList<String>();
			notMatchedPostStore.put(uid, postList);
		}
		postList.add(postId);
	}
	
	public List<String> getMatchedPosts(String uid) {
		List<String> postList = new ArrayList<String>();
		if(matchedPostStore.get(uid) != null)
			postList.addAll(matchedPostStore.get(uid));
		return postList;
	}
	
	public List<String> getNotMatchedPosts(String uid) {
		List<String> postList = new ArrayList<String>();
		if(notMatchedPostStore.get(uid) != null)
			postList.addAll(notMatchedPostStore.get(uid));
		return postList;
	}
	
	public void clearNotMatchedPosts(String uid) {
		notMatchedPostStore.remove(uid);
	}
	
	public void removeUser(String uid) {
		matchedPostStore.remove(uid);
		notMatchedPostStore.remove(uid);
	}
	
	public void clearAll() {
		matchedPostStore.clear();
		notMatchedPostStore.clear();
	}

}
