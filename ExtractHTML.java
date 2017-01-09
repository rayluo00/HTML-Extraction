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

	/* StringBuilders to hold data of HTML extraction. */
	private static StringBuilder linkBuilder;
	private static StringBuilder tagBuilder;
	private static StringBuilder sequenceBuilder;

	/**************************************************************************************
	 * Check if the website URL given from the first argument is a valid HTTP or HTTPS
	 * wesbite. If no transport protocol is provided, include the protocol 'http'. 
	 */
	private static String CheckWebsiteURL (String websiteURL) {
		String newURL = websiteURL;

		if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
			newURL = "http://"+websiteURL;
		}

		return newURL;
	}

	/**************************************************************************************
	 * Read the input stream from the URL connection and remove all the leading and
	 * trailing whitespaces. Ensure if the HTML line is wrapped in another line, it 
	 * will be appended to the origin of the correlating HTML tag. Adds all HTML 
	 * lines to an ArrayList to organize original structure and easily get specified 
	 * HTML lines.
	 */
	private static ArrayList<HtmlData> ConstructHtmlString (BufferedReader urlReader) throws IOException {
		String input;
		String htmlString = "";
		boolean inScript = false;
		ArrayList<HtmlData> htmlDataList = new ArrayList<HtmlData>();

		while ((input = urlReader.readLine()) != null) {
			if (!input.equals("")) {
				// Remove white space from HTML line and filter out the script codes.
				input = input.trim();
				input = input.replaceAll("<\\s*script[^>]*>(.*?)<\\s*/\\s*script>", "<script></script>");

				if (!inScript) {
					if (input.contains("<script")) {
						inScript = true;
					}

					if (!input.equals("") && input.charAt(input.length() - 1) != '>') {
						htmlString += input+" ";	
					} else {
						htmlString += input+" ";

						HtmlData data = new HtmlData();
						data.SetHtml(htmlString);
						htmlDataList.add(data);
						htmlString = "";
					}
				}

				// Don't recieve upcoming inputs until script it closed.
				if (input.contains("</script>") && inScript) {
					inScript = false;
				}
			}
		}

		return htmlDataList;
	}
	
	/**************************************************************************************
	 * Validate the website link string to check if the given string input is a valid
	 * website link. Used by FindLinks() as a validation when going through HTML lines.
	 */
	private static boolean ValidateWebsiteLinks (String htmlLink) {
		try {
			URL linkURL = new URL(htmlLink);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	/**************************************************************************************
	 * Split the HTML line with a regex to only recieve data that are between quotation
	 * marks. Then call the ValidateWebsiteLink() to validate if the split string is a
	 * valid website link. The linkBuilder is appende with the website links founded
	 * from the HTML line.
	 */
	private static void FindLinks (String htmlLine) {
		String htmlLink;
		Pattern	linkRegex = Pattern.compile("\"(.*?)\"", Pattern.CASE_INSENSITIVE);
		Matcher linkMatch = linkRegex.matcher(htmlLine);

		while (linkMatch.find()) {
			htmlLink = linkMatch.group(1).toString();

			if (ValidateWebsiteLinks(htmlLink)) {
				linkBuilder.append(htmlLink+"\r\n");
			}
		}
	}

	/**************************************************************************************
	 * Remove any punctuations from the String word that is used by the FindSequence()
	 * method. 
	 */
	private static String RemovePunctuations (String word) {
		String newWord = "";

		for (Character letter : word.toCharArray()) {
			if (Character.isLetterOrDigit(letter)) {
				newWord += letter;
			}
		}

		return newWord;
	}

	/**************************************************************************************
	 * Split the HTML line with a regex to only recieve the data outside of HTML tags.
	 * Then the regex result is split to check each word if it starts with an uppercase
	 * letter. When a capatalized word is found, append it to the sequence string and
	 * increment the sequenceSize. A valid sequence is when the sequenceSize is greater
	 * than two words. The sequenceBuilder is appended with the sequences founded from the
	 * HTML line.
	 */
	private static void FindSequences (String htmlLine) {
		int sequenceSize;
		String sequence;

		// Go through all the text between HTML tags and check if the words
		// in each HTML line is capatalized and valid for a sequence.
		for (String sentence : htmlLine.split("<[^>]+>")) {
			sequence = new String();
			sequenceSize = 0;
			
			for (String word : sentence.split("[~`,./<>?;':/[/]{}!@#$%^()-/_\\|\\s]")) {
				if (!word.equals("")) {
					if (word.length() > 1 && Character.isUpperCase(word.charAt(0))) {
						word = RemovePunctuations(word);
						sequence += word + " ";
						sequenceSize++;
					} else {
						if (sequenceSize > 1) {
							sequenceBuilder.append(sequence+"\r\n");
						}
						sequence = "";
						sequenceSize = 0;
					}
				}
			}

			// Check if the remaining sequence is valid and append it to 
			// the StringBuilder of possible sequences.
			if (sequenceSize > 1) {
				sequenceBuilder.append(sequence+"\r\n");
			}
		}
	}

	/**************************************************************************************
	 * Split the HTML line using a regex that extracts the data between the '<' and '>'.
	 * Split the string to only recieve the name of the HTML tag and reformat the tag
	 * if there is a forward-flash at the end of the tag after the split. The tagBuilder
	 * is appended with all the HTML tags founds from the current HTML line.
	 */
	private static void FindTags (String htmlLine) {
		String htmlTag;
		boolean hasForwSlash;
		Pattern tagRegex = Pattern.compile("<(.)*?>", Pattern.CASE_INSENSITIVE);
		Matcher tagMatch = tagRegex.matcher(htmlLine);

		while (tagMatch.find()) {
			hasForwSlash = false;
			htmlTag = tagMatch.group(0).toString();
			
			// Ensure the HTML tag isn't a comment.
			if (htmlTag.charAt(1) != '!') {
				if (htmlTag.charAt(htmlTag.length() - 2) == '/') {
					hasForwSlash = true;
				}

				htmlTag = htmlTag.split("\\s+")[0];

				if (htmlTag.charAt(htmlTag.length() - 1) != '>') {
					if (hasForwSlash) {
						htmlTag += "/";
					}
					htmlTag += ">";
				}

				tagBuilder.append(htmlTag);
			}
		}
	}

	/**************************************************************************************
	 * Iterate through the ArrayList of HTML lines and find the links, sequences, and
	 * tags for each line. 
	 */
	private static void ParseHtml (ArrayList<HtmlData> htmlDataList) {
		int lslens = htmlDataList.size();
		String htmlLine;

		for (int i = 0; i < lslens; i++) {
			htmlLine = htmlDataList.get(i).GetHtml();

			FindLinks(htmlLine);
			FindSequences(htmlLine);
			FindTags(htmlLine);
		}
	}

	/**************************************************************************************
	 * Write the results after parsing through the HTML document into an output file
	 * with the given name from the second argument passed into the program (args[1]).
	 * The StringBuilders (linkBuilder, tagBuilder, and sequenceBuilder) will be 
	 * appended to the output text file. The use of '\r\n' is to create a new line in
	 * the output text file for Windows applications (Notepad) that opens the text file.
	 */
	private static void WriteToFile (String outputFileName) {
		int charPerLine = 0;
		int totalTagLength = tagBuilder.length();

		try {
			PrintWriter outputWriter = new PrintWriter(new FileWriter("./OutputTxtFiles/"+outputFileName));

			outputWriter.append("============================= LINKS =============================\r\n");
			outputWriter.append(linkBuilder.toString());
			outputWriter.append("\r\n\r\n============================= HTML ==============================\r\n");

			// Reformat the HTML tag output to have a maximum of 85 characters
			// per line to improve readability in the output text file.
			while (charPerLine + 85 < tagBuilder.length() && 
							(charPerLine = tagBuilder.lastIndexOf(">", charPerLine + 85)) != -1) {
				tagBuilder.replace(charPerLine, charPerLine + 1, ">\r\n");
			}

			outputWriter.append(tagBuilder.toString());
			outputWriter.append("\r\n\r\n=========================== SEQUENCES ===========================\r\n");
			outputWriter.append(sequenceBuilder.toString());

			outputWriter.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Unable to locate the directory or file.");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: I/O exception when writing into text file.");
			//e.printStackTrace();
		}
	}

	/**************************************************************************************
	 * Main function that takes in two arguments as input for URL connection and output
	 * file destination (args[0] = HTML Website URL | args[1] = output file name). A URL
	 * connection will be established to get the InputStream and read the HTML website
	 * for it's HTML document.
	 */
	public static void main (String[] args) {
		//File outputDir = new File("OutputTxtFiles");
		String website = args[0];
		String htmlString;
		String outputFileName = args[1];
		ArrayList<HtmlData> htmlDataList;

		linkBuilder = new StringBuilder();
		tagBuilder = new StringBuilder();
		sequenceBuilder = new StringBuilder();
		
		website = CheckWebsiteURL(website);
		System.out.println("Connect to: "+website+"\n");
		
		try {
			// Uncomment code blow if computer can not run Makefiles to create
			// the output directory 'OutputTxtFiles'. And 'outputDir' variable.

			/*if (!outputDir.exists()) {
				mkdir(outputDir);
			}*/

			URL siteUrl = new URL(website);
			URLConnection urlConnection = siteUrl.openConnection();
			BufferedReader urlReader = new BufferedReader(
							new InputStreamReader(urlConnection.getInputStream()));
			
			htmlDataList = ConstructHtmlString(urlReader);
			urlReader.close();

			ParseHtml(htmlDataList);
			WriteToFile(outputFileName);

		} catch (MalformedURLException e) {
			System.out.println("ERROR: Problem with the URL of the website.\n");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: Website server issue.\n");
			//e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println("ERROR: Can not create the output directory.\n");
			//e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("ERROR: Reading from a input stream that is a null pointer.\n");
			//e.printStackTrace();
		}
	}
}
