import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ExtractHTML {

	private static String ValidateWebsiteURL (String websiteURL) {
		String newURL = websiteURL;

		if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
			newURL = "http://"+websiteURL;
		}

		return newURL;
	}

	private static ArrayList<HtmlData> ConstructHtmlString (BufferedReader urlReader) throws IOException {
		String htmlString = new String();
		String input;
		int inputLength;
		ArrayList<HtmlData> htmlDataList = new ArrayList<HtmlData>();

		while ((input = urlReader.readLine()) != null) {
			//System.out.println(input);
			if (!input.equals("")) {
				input = input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");

				//System.out.println("--OVER HERE--\'"+input+"\'--OVER HERE--\n\n");

				inputLength = input.length();

				if (inputLength > 0 && input.charAt(inputLength - 1) != '>') {
					htmlString += input;	
				} else {
					htmlString += input;

					HtmlData data = new HtmlData();
					data.SetHtml(htmlString);
					htmlDataList.add(data);
					htmlString = "";
				}
			}
		}

		return htmlDataList;
	}

	private static void FindLinks (String htmlLinks) {
		
	}

	private static void FindSequences (String htmlSequence) {
		String sequence;
		int sequenceSize;
		LinkedList<String> validSequence = new LinkedList<String>();

		// Go through all the text between HTML tags
		for (String sentence : htmlSequence.split("<[^>]+>")) {
			sequence = new String();
			sequenceSize = 0;
			//System.out.println("SENTENCE: \'"+sentence+"\'");
			// Go through each word in a valid text line.
			for (String word : sentence.split("[~`,./<>?;':/[/]{}!@#$%^()-/_\\|\\s]")) {
				if (!word.equals("")) {
					if (Character.isUpperCase(word.charAt(0)) && word.length() > 1) {
						//System.out.println("WORD: \'"+word+"\'");
						sequence += word + " ";
						sequenceSize++;
					} else {
						//System.out.println("NOT WORD: \'"+word+"\'");

						if (sequenceSize > 1) {
							System.out.println("VALID: \'"+sequence+"\'");
						}
						sequence = "";
						sequenceSize = 0;
					}
				}
			}

			if (sequenceSize > 1) {
				System.out.println("VALID: \'"+sequence+"\'");
			}
		}
	}

	private static void FindTags (String htmlTag) {
		boolean hasClosingTag = false;

		if (htmlTag.charAt(1) != '!') {
			if (htmlTag.charAt(htmlTag.length() - 2) == '/') {
				hasClosingTag = true;
			}

			htmlTag = htmlTag.split("\\s+")[0];

			if (htmlTag.charAt(htmlTag.length() - 1) != '>') {
				if (hasClosingTag) {
					htmlTag += "/";
				}

				htmlTag += ">";
			}

			System.out.println("FOUND: "+htmlTag);
		}
	}

	private static void ParseHtml (ArrayList<HtmlData> htmlDataList) {
		int lslens = htmlDataList.size();
		String[] tagReformatArray;
		Pattern tagRegex = Pattern.compile("<(.)*?>", Pattern.CASE_INSENSITIVE);
		String htmlLine;

		for (int i = 0; i < lslens; i++) {
			htmlLine = htmlDataList.get(i).GetHtml();

			Matcher tagMatch = tagRegex.matcher(htmlLine);
			System.out.println(htmlLine+"|END|");

			FindLinks(htmlLine);
			FindSequences(htmlLine);

			while (tagMatch.find()) {
				htmlLine = tagMatch.group().toString();
				FindTags(htmlLine);
			}
			System.out.println("\n");
		}
	}
	
	public static void main (String[] args) {
		String htmlString;
		String website = args[0];
		String outputFileName = args[1];
		ArrayList<HtmlData> htmlDataList;
		
		website = ValidateWebsiteURL(website);
		System.out.println("Connect to: "+website);
		
		try {
			PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFileName, true));
			URL siteUrl = new URL(website);
			URLConnection urlConnection = siteUrl.openConnection();
			BufferedReader urlReader = new BufferedReader(
							new InputStreamReader(urlConnection.getInputStream()));
			
			htmlDataList = ConstructHtmlString(urlReader);

			urlReader.close();

			ParseHtml(htmlDataList);
		} catch (MalformedURLException e) {
			//System.out.println("ERROR: Problem with the URL of the website.");
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("ERROR: I/O exception.");
			e.printStackTrace();
		}
	}
}
