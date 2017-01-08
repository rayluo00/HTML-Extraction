/* ExtractHTML.java
 *
 * Author: Raymond Weiming Luo
 * HTML Extraction Program
 *
 * This program is given a website as the  first argument and will 
 * connect onto a HTML or HTMLS website to retrieve the HTML document.
 * The document will be parsed to extract the links, HTML tags, and a
 * sequence of words (series of characters seperated by whitespace)
 * that starts with an uppercase letter. After parsing the HTML
 * document, the output will be written into a text file with the file
 * name correlating to the second argument given to the program.
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ExtractHTML {

	private static StringBuilder linkBuilder;
	private static StringBuilder tagBuilder;
	private static StringBuilder sequenceBuilder;

	private static String CheckWebsiteURL (String websiteURL) {
		String newURL = websiteURL;

		if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
			newURL = "http://"+websiteURL;
		}

		return newURL;
	}

	private static ArrayList<HtmlData> ConstructHtmlString (BufferedReader urlReader) throws IOException {
		String htmlString = new String();
		String input;
		ArrayList<HtmlData> htmlDataList = new ArrayList<HtmlData>();

		while ((input = urlReader.readLine()) != null) {
			//System.out.println(input);
			if (!input.equals("")) {
				input = input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");

				//System.out.println("--OVER HERE--\'"+input+"\'--OVER HERE--\n\n");

				if (!input.equals("") && input.charAt(input.length() - 1) != '>') {
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

	private static boolean ValidateWebsiteLinks (String htmlLink) {
		try {
			URL linkURL = new URL(htmlLink);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	private static void FindLinks (String htmlLine) {
		String htmlLink;
		Pattern	linkRegex = Pattern.compile("\"(.*?)\"", Pattern.CASE_INSENSITIVE);
		Matcher linkMatch = linkRegex.matcher(htmlLine);

		while (linkMatch.find()) {
			htmlLink = linkMatch.group(1).toString();

			//System.out.println("FOUND: \'"+htmlLink+"\'");

			if (ValidateWebsiteLinks(htmlLink)) {
				//System.out.println("LINK : \'"+htmlLink+"\'");
				linkBuilder.append(htmlLink+"\r\n");
			}
		}
	}

	private static void FindSequences (String htmlLine) {
		int sequenceSize;
		String sequence;
		LinkedList<String> validSequence = new LinkedList<String>();

		// Go through all the text between HTML tags
		for (String sentence : htmlLine.split("<[^>]+>")) {
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
							sequenceBuilder.append(sequence+"\r\n");
							//System.out.println("VALID: \'"+sequence+"\'");
						}
						sequence = "";
						sequenceSize = 0;
					}
				}
			}

			if (sequenceSize > 1) {
				sequenceBuilder.append(sequence+"\r\n");
				//System.out.println("VALID: \'"+sequence+"\'");
			}
		}
	}

	private static void FindTags (String htmlLine) {
		String htmlTag;
		boolean hasClosingTag = false;
		Pattern tagRegex = Pattern.compile("<(.)*?>", Pattern.CASE_INSENSITIVE);
		Matcher tagMatch = tagRegex.matcher(htmlLine);
		
		while (tagMatch.find()) {
			htmlTag = tagMatch.group().toString();

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
				tagBuilder.append(htmlTag);
				//System.out.println("FOUND: "+htmlTag);
			}
		}
	}

	private static void ParseHtml (ArrayList<HtmlData> htmlDataList) {
		int lslens = htmlDataList.size();
		String htmlLine;

		for (int i = 0; i < lslens; i++) {
			htmlLine = htmlDataList.get(i).GetHtml();
			//System.out.println(htmlLine+"|END|");

			FindLinks(htmlLine);
			FindSequences(htmlLine);
			FindTags(htmlLine);

			//System.out.println("\n");
		}
	}

	private static void WriteToFile (String outputFileName) throws IOException {
		int charPerLine = 0;
		int totalTagLength = tagBuilder.length();
		PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFileName));

		outputWriter.append("============================= LINKS =============================\r\n\r\n");
		outputWriter.append(linkBuilder.toString());
		outputWriter.append("\r\n\r\n============================= HTML ==============================\r\n\r\n");
		
		while (charPerLine + 85 < tagBuilder.length() && 
						(charPerLine = tagBuilder.lastIndexOf(">", charPerLine + 85)) != -1) {
			tagBuilder.replace(charPerLine, charPerLine + 1, ">\r\n");
		}
		
		outputWriter.append(tagBuilder.toString());
		outputWriter.append("\r\n\r\n=========================== SEQUENCES ===========================\r\n\r\n");
		outputWriter.append(sequenceBuilder.toString());

		outputWriter.close();
	}
	
	public static void main (String[] args) {
		String htmlString;
		String website = args[0];
		String outputFileName = args[1];
		ArrayList<HtmlData> htmlDataList;

		linkBuilder = new StringBuilder();
		tagBuilder = new StringBuilder();
		sequenceBuilder = new StringBuilder();
		
		website = CheckWebsiteURL(website);
		System.out.println("Connect to: "+website+"\n");
		
		try {
			URL siteUrl = new URL(website);
			URLConnection urlConnection = siteUrl.openConnection();
			BufferedReader urlReader = new BufferedReader(
							new InputStreamReader(urlConnection.getInputStream()));
			
			htmlDataList = ConstructHtmlString(urlReader);

			urlReader.close();

			ParseHtml(htmlDataList);
			WriteToFile(outputFileName);
			System.out.println("Program completed.");

		} catch (MalformedURLException e) {
			System.out.println("ERROR: Problem with the URL of the website.");
		} catch (IOException e) {
			System.out.println("ERROR: I/O exception.");
		}
	}
}
