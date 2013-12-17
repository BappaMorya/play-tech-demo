package cmn;

import java.util.List;

import models.BdayPost;

public class PostDataWrapper {
	
	private List<BdayPost> matched = null;
	
	private List<BdayPost> notMatched = null;
	
	private int matchedCount = 0;
	
	private int notMatchedCount = 0;
	
	private int totalCount = 0;
	
	private String bdayString;

	public String getBdayString() {
		return bdayString;
	}

	public void setBdayString(String bdayString) {
		this.bdayString = bdayString;
	}

	public int getNotMatchedCount() {
		return notMatchedCount;
	}

	public void setNotMatchedCount(int notMatchedCount) {
		this.notMatchedCount = notMatchedCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getMatchedCount() {
		return matchedCount;
	}

	public void setMatchedCount(int matchedCount) {
		this.matchedCount = matchedCount;
	}

	public List<BdayPost> getMatched() {
		return matched;
	}

	public void setMatched(List<BdayPost> matched) {
		this.matched = matched;
	}

	public List<BdayPost> getNotMatched() {
		return notMatched;
	}

	public void setNotMatched(List<BdayPost> notMatched) {
		this.notMatched = notMatched;
	}

}
