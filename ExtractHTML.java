import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ExtractHTML {

	private static String ValidateWebsiteURL (String websiteURL) {
		String newURL = new String();

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

				if (input.charAt(input.length() - 1) != '>') {
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

	private static void FindLinks (String htmlLine) {
	
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
		Pattern regex = Pattern.compile("<(.)*?>");
		String htmlTag;

		for (int i = 0; i < lslens; i++) {
			Matcher match = regex.matcher(htmlDataList.get(i).GetHtml());
			System.out.println(htmlDataList.get(i).GetHtml()+"|END|");

			while (match.find()) {
				htmlTag = match.group().toString();
				FindLinks(htmlTag);
				FindTags(htmlTag);
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
		
		try {
			PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFileName, true));
			URL siteUrl = new URL(website);
			HttpURLConnection siteConnection = (HttpURLConnection) siteUrl.openConnection();
			BufferedReader urlReader = new BufferedReader(new InputStreamReader(siteConnection.getInputStream()));
			
			htmlDataList = ConstructHtmlString(urlReader);

			urlReader.close();

			ParseHtml(htmlDataList);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: Problem with the URL of the website.");
		} catch (IOException e) {
			System.out.println("ERROR: I/O exception.");
		}
	}
}
