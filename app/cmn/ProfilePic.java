package cmn;

import com.restfb.Facebook;

public class ProfilePic {
	
	public ProfilePic() {}
	
	@Facebook
    public ProfilePicture data;

    public ProfilePicture getData() {
            return data;
    }
    public void setData(ProfilePicture data) {
            this.data = data;
    }
	
	public static class ProfilePicture {
		
		@Facebook("url")
		public String url;
		
		public ProfilePicture() {}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "ProfilePicture [url=" + url + "]";
		}
		
	}

	@Override
	public String toString() {
		return "ProfilePic [data=" + data + "]";
	}

}
