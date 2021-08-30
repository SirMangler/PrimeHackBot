package Utilities;

import java.util.Comparator;

/**
 * @author SirMangler
 *
 * @date 20 Oct 2020
 */
public class LengthComparator implements Comparator<String> {

	@Override
	public int compare(String a, String b) {
		if (a.length() == b.length()) return 0;
		
		return a.length() > b.length() ? 1 : -1;
	}

}
