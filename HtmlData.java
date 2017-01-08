/* HtmlData.java
 *
 * Author: Raymond Weiming Luo
 * HTML Data Object
 *
 * Object to hold the input after reading the input stream from the connected 
 * urlConnection. Stores the input as a String to easily store and access a 
 * structured ArrayList of the HTML document.
 */

public class HtmlData {
	private String htmlLine;

	public void SetHtml (String inputLine) { this.htmlLine = inputLine; }

	public String GetHtml () { return this.htmlLine; }
}
