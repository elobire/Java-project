import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class RacLaunch {

	private static WebClient webClient;

	static Set<String> urlList = new HashSet<>();
	static Set<RacRequest> descList = new HashSet<>();

	private static int searchLines = 0;
	private static int refLines = 0;

	private static final Pattern FILE_REGEX = Pattern
			.compile("products/(.+?)\">");

	private static final Pattern TAG_REGEX = Pattern
			.compile("<!--project start-->(?s)(.+?)<!--project end-->");
	private static final Pattern URL_REGEX = Pattern
			.compile("<a href=\"(?s)(.+?)\" title=");

	private static final Pattern DESC_REGEX = Pattern
			.compile("spacer25(?s)(.+?)spacer25");

	public static void go() throws IOException, SAXException {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				java.util.logging.Level.OFF);

		/* These strings define the login values for the website */
		final String USERNAME = "croak3r";
		final String PASSWORD = "killme2";
		countLines(); // counts the ammount of lines in the keyword documents
		System.out.println("Search file has " + searchLines);
		System.out.println("ref file has " + refLines);
		webClient = new WebClient(BrowserVersion.CHROME);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				java.util.logging.Level.OFF);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setJavaScriptEnabled(false);

		final HtmlPage page1 = webClient
				.getPage("http://www.rent-acoder.com/login.php");

		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("frmLogin");
		// System.out.println("Found form");

		// final HtmlSubmitInput button = form.getInputByName("submitbutton");
		final HtmlTextInput usernameField = form.getInputByName("loginName");
		final HtmlPasswordInput passwordField = (HtmlPasswordInput) form
				.getInputByName("passWord");
		System.out.println("Entering username " + USERNAME + " and password "
				+ PASSWORD);

		final HtmlSubmitInput submitButton = form.getInputByValue("Login");
		// Change the value of the text fields
		usernameField.setValueAttribute(USERNAME);
		passwordField.setValueAttribute(PASSWORD);

		/* try is needed as sometimes returns text page (not common) */
		try {
			final HtmlPage page2 = submitButton.click();
		} catch (java.lang.ClassCastException e) {
			System.out.println("Login button returned text page");
			final HtmlPage page2 = submitButton.click();

		}

		// System.out.println(page2.getUrl());
		// Now submit the form by clicking the button and get back the second
		// page.

		System.out.println("Attempting to search");

		/* use each word to form a search */
		BufferedReader searchReader = null;

		try {
			searchReader = new BufferedReader(new FileReader(new File(
					"searchWords.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Search file not found");
			e.printStackTrace();
		}

		String line;

		/* search for each keyword */
		while ((line = searchReader.readLine()) != null) {
			String searchString = null;

			Boolean searchEnd = false;
			int i = 1;
			/* search through each page number in resulting search */
			while (!searchEnd) {
				searchString = "http://www.rent-acoder.com/results.php?pag="
						+ i + "&keyword=" + line;

				HtmlPage searchPage = webClient.getPage(searchString);

				searchEnd = getResultURL(searchPage.asXml());
				i++;
			}
		}

		for (RacRequest descObj : RacRequest.racList) {
			System.out.println("Searching new object");
			try {
				searchReader = new BufferedReader(new FileReader(new File(
						"refWords.txt")));
			} catch (FileNotFoundException e) {
				System.out.println("Search file not found");
				e.printStackTrace();
			}

			// loop through refLines document, passing in words to horsepool
			// algorithm

			try {
				searchReader = new BufferedReader(new FileReader(new File(
						"refWords.txt")));
			} catch (FileNotFoundException e) {
				System.out.println("Search file not found");
				e.printStackTrace();
			}
			int i = 0;
			while (i < refLines) {
				i++;

				Horsepool horsepool = new Horsepool();

				String url = descObj.getUrl();
				String desc = findDescription(url);
				// System.out.println(desc);
				HtmlPage descPage = webClient.getPage(url);
				String page = descPage.asXml();
				// check if match occurs


				String word = searchReader.readLine();


				Boolean match = false;
				match = horsepool.runSearch(word, desc);

				if (!descObj.getMatch()) {
					descObj.setMatch(match);
				}
				if (match) { // increment hitcount if match found
					System.out.println("Incrementing hitcount");
					descObj.incrementHitCount();
				}
				if (descObj.getMatch()) {
					System.out.println("Found match with word: " + word);
				} else {
					System.out.println("Match not found with word: " + word);
				}

			}
			HtmlPage descPage = webClient.getPage(descObj.getUrl());
			findFiles(descPage.asXml(), descObj);
		}

		System.out.println("Finished searching through results");

		/*
		 * This loop looks at each object file and compares them against the
		 * user selected file
		 */
		for (RacRequest descObj : RacRequest.racList) {
			String userFileString = FileUtils
					.readFileToString(Interface.assignmentFile);
			if (descObj.getFile() != null && isTextFile(descObj.getFile())) {
				BodyContentHandler handler = new BodyContentHandler(-1);

				Metadata metadata = new Metadata();
				FileInputStream inputstream = new FileInputStream(
						descObj.getFile());
				ParseContext pcontext = new ParseContext();

				AutoDetectParser parser = new AutoDetectParser();

				try {
					parser.parse(inputstream, handler, metadata, pcontext);
				} catch (TikaException e) {
					e.printStackTrace();
				}

				BodyContentHandler cHandler = new BodyContentHandler(-1);

				Metadata cMetadata = new Metadata();
				FileInputStream cInputstream = new FileInputStream(
						Interface.assignmentFile);
				ParseContext cPcontext = new ParseContext();

				AutoDetectParser cParser = new AutoDetectParser();

				try {
					cParser.parse(cInputstream, cHandler, cMetadata, cPcontext);
				} catch (TikaException e) {
					e.printStackTrace();
				}
				String cFileString = cHandler.toString();
				String foundFileString = handler.toString();

				Boolean match = false;
				Horsepool horsepool = new Horsepool();
				match = horsepool.runSearch(foundFileString, cFileString);

				if (match) {
					System.out.println("Files match");
					descObj.setFileMatched(true);
				}
			}
		}

	}

	/* gathers all the URL's of the search page results */
	private static Boolean getResultURL(final String str) {
		final List<String> tagValues = new ArrayList<String>();
		final Matcher matcher = TAG_REGEX.matcher(str);
		Boolean searchEnd = false;
		while (matcher.find()) {
			tagValues.add(matcher.group(1));
		}
		if (tagValues.isEmpty()) {
			System.out.println("No results found");
			searchEnd = true;
		}

		for (String urlString : tagValues) {
			Matcher urlMatcher = URL_REGEX.matcher(urlString);

			if (urlMatcher.find()) {
				System.out.println("Url found");
				RacRequest desc = new RacRequest();
				desc.setUrl(urlMatcher.group(1));
				RacRequest.racList.add(desc);
			} else {
				System.out.println("End of search pages");
			}

		}

		System.out
				.println("List has " + RacRequest.racList.size() + " objects");
		return searchEnd;
	}

	/*
	 * Find all files on the project page. all files are save for further
	 * analysis
	 */
	private static void findFiles(final String page, RacRequest descObj)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		final Matcher matcher = FILE_REGEX.matcher(page);
		while (matcher.find()) {

			String filename = matcher.group(1);
			System.out.println("File found:" + filename);
			Page filePage = webClient
					.getPage("http://www.rent-acoder.com/products/" + filename);

			try {
				InputStream iStream = filePage.getWebResponse()
						.getContentAsStream();
				try {
					File f = new File(filename);
					OutputStream os = new FileOutputStream(f);
					byte[] bytes = new byte[1024];
					int read = 0;

					while ((read = iStream.read(bytes)) != -1) {
						os.write(bytes, 0, read);
					}
					os.close();
					iStream.close();
					descObj.setFile(f);
					descObj.setFoundFile(true);
					System.out.println("File saved");
				} catch (IOException ex) {
					// Exception handling
				}
			} catch (IOException ex) {
				// Exception handling
			}
			Interface.repaintTable();
		}
	}

	/*
	 * Counts the amount of lines in the keyword documents. This is needed for
	 * the loops in the class
	 */
	private static void countLines() throws IOException {
		BufferedReader refReader = null;
		BufferedReader searchReader = null;
		try {
			refReader = new BufferedReader(new FileReader(new File(
					"refWords.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Refined search word file not found");
			e.printStackTrace();
		}
		try {
			searchReader = new BufferedReader(new FileReader(new File(
					"searchWords.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Initial search word file not found");
			e.printStackTrace();
		}
		refLines = (int) refReader.lines().count();
		searchLines = (int) searchReader.lines().count();
		refReader.close();
		searchReader.close();
	}

	/*
	 * Find the description section of the project page This is then later used
	 * to conduct a search with refined keywords
	 */
	private static String findDescription(String url)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		HtmlPage RacRequest = webClient.getPage(url);
		String page = RacRequest.asXml();

		String description = null;
		Matcher matcher = DESC_REGEX.matcher(page);
		if (matcher.find()) {
			description = matcher.group(1);
		} else {
			System.out.println("Description not found!");
		}

		return description;

	}

	private static boolean isTextFile(File file) {
		String ext = FilenameUtils.getExtension(file.getPath());
		System.out.println(ext);
		if (ext.equals("txt") | ext.equals("pdf") | ext.equals("doc")
				| ext.equals("docx")) {
			return true;
		}
		return false;
	}

}
