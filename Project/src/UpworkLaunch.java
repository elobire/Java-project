import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class UpworkLaunch {
	/* This class searches through the upwork.com website */
	
	private static WebClient webClient;

	
	private static final Pattern URL_REGEX = Pattern
			.compile("ciphertext\":\"(?s)(.+?)\",\"desc");
	private static final Pattern DESC_REGEX = Pattern
			.compile("name=\"Description\" content=\"(?s)(.+?)\"/>");
	private static final Pattern FILE_REGEX = Pattern
			.compile("https://www.upwork.com(?s)(.+?)\" target=");
	private static final Pattern FILE_NAME_REGEX = Pattern
			.compile("%3D%22(?s)(.+?)%22");
	
	
	
	private static int searchLines = 0;
	private static int refLines = 0;
	
		public static void go() throws FailingHttpStatusCodeException, MalformedURLException, IOException, SAXException {
			countLines();
		

		 webClient = new WebClient(BrowserVersion.CHROME);

		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				java.util.logging.Level.OFF);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setJavaScriptEnabled(false);

		final HtmlPage page1 = webClient
				.getPage("https://www.upwork.com/Login");

		// find login form
		final HtmlForm form = page1.getFormByName("login");
		final HtmlTextInput usernameField = form
				.getInputByName("login[username]");
		final HtmlPasswordInput passwordField = (HtmlPasswordInput) form
				.getInputByName("login[password]");

		System.out.println("found textboxes");
		HtmlElement button = (HtmlElement) page1.createElement("button");
		button.setAttribute("type", "submit");

		usernameField.setValueAttribute("croak3r");
		passwordField.setValueAttribute("thedevil1");
		form.appendChild(button);

		final HtmlPage page2 = button.click();

		System.out.println(page2.getUrl());
		
		
		
		BufferedReader searchReader = null;

		try {
			searchReader = new BufferedReader(new FileReader(new File(
					"searchWords.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Search file not found");
			e.printStackTrace();
		}

		String line;

		while ((line = searchReader.readLine()) != null) {
			String searchString = null;
	

			// now go through each possible page in result
			
				Boolean end = false;
				int i = 1;
				while(end == false) {
					
				searchString = "https://www.upwork.com/o/jobs/browse/?page=" + i + "&q="+ line +"&sort=create_time%2Bdesc";
				
				HtmlPage searchPage = webClient.getPage(searchString);
				String url = searchPage.getUrl().toString();
				if(url.trim().equals(searchString) || i ==1) {
					i++;
					System.out.println("String matched");
					getResultURL(searchPage.asXml());
				}else{
					System.out.println("End of search list " + url);
					end = true;
					
				}
			}
		}	
		
		
		/* search the description of each page for matches */
		for(UpworkRequest reqObj :UpworkRequest.upworkList){
			System.out.println("Searching new Upwork object");
			try {
				searchReader = new BufferedReader(new FileReader(new File(
						"refWords.txt")));
			} catch (FileNotFoundException e) {
				System.out.println("Search file refWords.txt not found");
				e.printStackTrace();
			}
			int i = 0;
			while (i < refLines) {
				i++;

				Horsepool horsepool = new Horsepool();
				String url = reqObj.getUrl();
				String desc = findDescription(url);
				// check if match occurs


				String word = searchReader.readLine();

				// changed from page

				Boolean match = false;
				match = horsepool.runSearch(word, desc);

				if (!reqObj.getMatch()) {
					reqObj.setMatch(match);
				}
				if (match) { // increment hitcount if match found
					System.out.println("Incrementing hitcount");
					reqObj.incrementHitCount();
				}
				if (reqObj.getMatch()) {
					System.out.println("Found match with word: " + word);
				} else {
					System.out.println("Match not found with word: " + word);
				}

			}
			HtmlPage reqPage = webClient.getPage(reqObj.getUrl());

			findFiles(reqPage.asXml(), reqObj);
			
			
			
		}
		System.out.println("Finished searching through results");
		
		/*Compare found file with user selected file (text only) */
		for (UpworkRequest upworkReq : UpworkRequest.upworkList) {
			String userFileString = FileUtils
					.readFileToString(Interface.assignmentFile);
			// System.out.println(userFileString);
			if (upworkReq.getFile() != null && isTextFile(upworkReq.getFile())) {
				System.out.println("Comparind " + upworkReq.getFile().getName());
				BodyContentHandler handler = new BodyContentHandler(-1);

				Metadata metadata = new Metadata();
				FileInputStream inputstream = new FileInputStream(
						upworkReq.getFile());
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
				if ( foundFileString.length() < 9000) {
				Horsepool horsepool = new Horsepool();
				match = horsepool.runSearch(foundFileString, cFileString);
				} else {
					System.out.println("file is too long");
				}
				if (match) {
					System.out.println("Files match");
					upworkReq.setFileMatched(true);
				}
			}
		}
		
		
	}
	
	/* Get page urls from initial search */
	private static void getResultURL(final String str) {
		final List<String> tagValues = new ArrayList<String>();
		final Matcher matcher = URL_REGEX.matcher(str);
		while (matcher.find()) {
			UpworkRequest request = new UpworkRequest();
			request.setUrl("https://www.upwork.com/jobs/_" + matcher.group(1));
			UpworkRequest.upworkList.add(request);
		}
		System.out.println("List has " + UpworkRequest.upworkList.size() + " objects");
	}
	
	/* count number of lines in documents */
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
	
	/* find the description of the project request page */
	private static String findDescription(String url)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		HtmlPage descPage = webClient.getPage(url);
		String page = descPage.asXml();

		String description = null;
		Matcher matcher = DESC_REGEX.matcher(page);
		if (matcher.find()) {
			
			description = matcher.group(1);
		} else {
			System.out.println("Description not found!");
		}
		return description;
	}
	
	/* find all files on project request */
	private static void findFiles(final String page, UpworkRequest reqObj)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		System.out.println("Searching for file in: " + reqObj.getUrl());
		final Matcher matcher = FILE_REGEX.matcher(page);
		if (matcher.find()) {
			String filename = matcher.group(1);
			System.out.println("File found:" + filename);
			Page filePage = null;
			try{
				filePage = webClient
					.getPage("https://www.upwork.com" + filename);	
			}catch (java.net.SocketTimeoutException e) {
				filePage = webClient
						.getPage("https://www.upwork.com" + filename);	
				
			}
				
			
			String url = filePage.getUrl().toString();
			
			String finalFilename = null;
			final Matcher urlMatcher = FILE_NAME_REGEX.matcher(url);
			if (urlMatcher.find()) {
				finalFilename = urlMatcher.group(1);
				System.out.println("Matched filename" + finalFilename);
			}else{
				System.out.println("Could not find filename");
			}
			
			
			System.out.println(url);

			try {
				InputStream iStream = filePage.getWebResponse()
						.getContentAsStream();
				
				try {
					System.out.println("Saving file");
					System.out.println();
					File f = new File(finalFilename);
					OutputStream os = new FileOutputStream(f);
					byte[] bytes = new byte[1024];
					int read = 0;

					while ((read = iStream.read(bytes)) != -1) {
						os.write(bytes, 0, read);
					}
					os.close();
					iStream.close();
					reqObj.setFile(f);
					reqObj.setFoundFile(true);
					System.out.println("File saved");
				} catch (IOException ex) {
					// Exception handling
				}
			} catch (IOException ex) {
				// Exception handling
			}
		}else{
			System.out.println("File not found");
		}
	}
	
	
	/* Check if found file is text file
	 * Files will not be compared if not text */
	private static boolean isTextFile(File file) {
		String ext = FilenameUtils.getExtension(file.getPath());
		System.out.println("file extension is " + ext);
		if (ext.equals("txt") | ext.equals("pdf") | ext.equals("doc") | ext.equals("docx")) {
			return true;
		}
		return false;
	}

}
