package models;

public class BdayPost {
	
	public String friendName;
	
	public String profilPicUrl;
	
	public String postId;
	
	public String postData;

	public String getProfilPicUrl() {
		return profilPicUrl;
	}

	public void setProfilPicUrl(String profilPicUrl) {
		this.profilPicUrl = profilPicUrl;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}

	@Override
	public String toString() {
		return "BdayPost [friendName=" + friendName + ", profilPicUrl="
				+ profilPicUrl + ", postId=" + postId + ", postData="
				+ postData + "]";
	}

}
