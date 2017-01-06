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

	private static void FindLinks (ArrayList<HtmlData> htmlDataList) {
		int lslens = htmlDataList.size();
		Pattern regex = Pattern.compile("<(.)*?>");

		for (int i = 0; i < lslens; i++) {
			Matcher match = regex.matcher(htmlDataList.get(i).GetHtml());
			System.out.println(htmlDataList.get(i).GetHtml()+"|END|");

			while (match.find()) {
				System.out.println("FOUND: "+match.group());
			}
			System.out.println("\n\n");
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
			BufferedReader urlReader = new BufferedReader(new InputStreamReader(siteUrl.openStream()));
			
			htmlDataList = ConstructHtmlString(urlReader);

			urlReader.close();

			FindLinks(htmlDataList);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: Problem with the URL of the website.");
		} catch (IOException e) {
			System.out.println("ERROR: I/O exception.");
		}
	}
}
