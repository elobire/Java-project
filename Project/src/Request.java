import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Request {
	/*
	 * Class contains data for essay mill request Should be extended for
	 * specific implementations for each essay mill
	 */

	private String url;
	private String description;

	@Override
	public String toString() {
		return "Request [url=" + url + ", description=" + description
				+ ", match=" + match + ", foundFile=" + foundFile
				+ ", hitCount=" + hitCount + ", file=" + file
				+ ", fileMatched=" + fileMatched + "]\n";
	}

	private Boolean match; // set to true if matches with search
	private Boolean foundFile;
	private int hitCount;
	private File file;
	private Boolean fileMatched;

	public Request() {
		url = null;
		setDescription(null);
		setMatch(false);
		setHitCount(0);
		setFoundFile(false);
		file = null;
		setFileMatched(false);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getMatch() {
		return match;
	}

	public void setMatch(Boolean match) {
		this.match = match;
	}

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public void incrementHitCount() {
		this.hitCount++;
	}

	public Boolean getFoundFile() {
		return foundFile;
	}

	public void setFoundFile(Boolean foundFile) {
		this.foundFile = foundFile;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		Request page = (Request) obj;
		System.out.println("Equals invoked");
		if (this.getUrl().equals(page.getUrl())) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getFileMatched() {
		return fileMatched;
	}

	public void setFileMatched(Boolean fileMatched) {
		this.fileMatched = fileMatched;
	}

}
