make:
	mkdir -p OutputTxtFiles
	javac ExtractHTML.java HtmlData.java

clean:
	$(RM) *.class , *~ , -rf OutputTxtFiles

run:
	@echo "Running program...\n"
	java ExtractHTML pitchbook.com/about-pitchbook pitchbook_output.txt
	java ExtractHTML https://www.youtube.com youtube_output.txt
	java ExtractHTML nltk.org/book/ch01.html nltk_output.txt
	java ExtractHTML https://github.com github_output.txt
	java ExtractHTML google.com google_output.txt
	java ExtractHTML https://mywestern.wwu.edu wwu_output.txt
	java ExtractHTML https://en.wikipedia.org/wiki/Main_Page wiki_ouput.txt
	java ExtractHTML https://yahoo.com yahoo_output.txt
	java ExtractHTML xkcd.com xkcd_output.txt
	java ExtractHTML https://microsoft.com microsoft_output.txt
	java ExtractHTML apple.com apple_output.txt
	java ExtractHTML pitchbook.com pitchbook_output2.txt
	java ExtractHTML https://android.com android_output.txt
	java ExtractHTML https://twitter.com twitter_output.txt
	java ExtractHTML https://destroyallsoftware.com/talks/wat wat_output.txt
	java ExtractHTML seahawks.com seahawks_output.txt
	java ExtractHTML https://aws.amazon.com aws_output.txt
	java ExtractHTML ebay.com ebay_output.txt
	java ExtractHTML https://yelp.com yelp_output.txt
	java ExtractHTML https://www.linkedin.com linkedin_output.txt
	@echo "Program finished."
