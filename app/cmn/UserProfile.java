package cmn;

public class UserProfile {
	
	public String userId;
	
	public String userName;
	
	public String birthDate;

	public UserProfile(String userId, String userName, String birthDate) {
		this.userId = userId;
		this.userName = userName;
		this.birthDate = birthDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return "UserProfile [userId=" + userId + ", userName=" + userName
				+ ", birthDate=" + birthDate + "]";
	}

}
