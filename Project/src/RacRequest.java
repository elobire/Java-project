import java.util.HashSet;
import java.util.Set;


public class RacRequest extends Request {
	static Set<RacRequest> racList = new HashSet<>();
	@Override
	public boolean equals(Object obj) {
		
		RacRequest page = (RacRequest) obj;
		System.out.println("Equals invoked");
		if (this.getUrl().equals(page.getUrl())){
		return true;
		} else {
			return false;
		}
	}

}
