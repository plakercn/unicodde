package unicode;

import java.util.*;;

public class MapKeyComParator implements Comparator<String> {
	@Override
	public int compare(String str1,String str2) {
	//	return str1.compareToIgnoreCase(str2);
		return str1.compareTo(str2);
	}

}
