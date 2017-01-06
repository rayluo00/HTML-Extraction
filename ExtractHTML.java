import java.io.*;
import java.net.*;
import java.util.*;

public class ExtractHTML {
	
	public static void main (String[] args) {
		String website = args[0];
		String outputFileName = args[1];

		if (!website.startsWith("http://") && !website.startsWith("https://")) {
			website = "http://"+website;
		}
		
		try {
			URL siteUrl = new URL(website);
			BufferedReader urlReader = new BufferedReader(new InputStreamReader(siteUrl.openStream()));
			
			String input;
			while ((input = urlReader.readLine()) != null) {
				System.out.println(input);	
			}

			urlReader.close();
		} catch (MalformedURLException e) {
			System.out.println("ERROR: main() problem when getting the URL of the website.");
		} catch (IOException e) {
			System.out.println("ERROR: main() I/O exception.");
		}
	}
}
