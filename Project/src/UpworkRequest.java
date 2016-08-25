import java.util.HashSet;
import java.util.Set;

/**
 * 
 */

/**
 * @author Ayrton
 *
 */
public class UpworkRequest extends Request {
	static Set<UpworkRequest> upworkList = new HashSet<>();
	@Override
	public boolean equals(Object obj) {
		
		UpworkRequest page = (UpworkRequest) obj;
		System.out.println("Equals invoked");
		if (this.getUrl().equals(page.getUrl())){
		return true;
		} else {
			return false;
		}
	}
}
