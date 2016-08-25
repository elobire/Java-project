public class Horsepool {
	/*
	 * This class contains the Boyer-Moore-Horsepool string matching algorithm
	 */
	public static int T_SIZE = 15000; // max size of skip table
	public static int table[] = new int[T_SIZE]; // skip table

	/* generates bad character match skip table */
	public void generateTable(String pattern) {

		char p[] = pattern.toCharArray();
		for (int i = 0; i < 8999; i++) {
			table[i] = p.length;
		}
		for (int i = 0; i < p.length - 1; i++) {
			table[p[i]] = p.length - 1 - i;
		}

	}

	/* Algorithm to conduct search */
	public int horesepoolAlgorithm(String sourceText, String pattern) {
		char s[] = sourceText.toCharArray();
		char p[] = pattern.toCharArray();
		int skip = 0;
		int i;
		sourceText.length();
		while (sourceText.length() - skip >= pattern.length()) {
			i = pattern.length() - 1;
			while (s[skip + i] == p[i]) {
				if (i == 0) {
					return skip;
				}
				i -= 1;

			}
			skip = skip + table[s[skip + pattern.length() - 1]];

		}
		return -1;
	}

	/*
	 * This method is called to run the search user must pass in the pattern to
	 * be searched for and the text to search in Will return false if pattern
	 * not found and true if found, also printing out the position in the source text
	 */
	public Boolean runSearch(String pattern, String source) {
		int resultPos;
		Horsepool horseObj = new Horsepool();

		horseObj.generateTable(pattern);
		resultPos = horseObj.horesepoolAlgorithm(source, pattern);

		if (resultPos == -1) {
			System.out.println("Pattern not found in source text");
			return false;
		} else {
			System.out.println("Pattern found at pos " + resultPos);
			return true;
		}
	}
}
