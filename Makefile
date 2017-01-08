make:
	javac ExtractHTML.java HtmlData.java

clean:
	$(RM) *.class , *~ , *.txt

run:
	@echo "Running program...\n"
	java ExtractHTML pitchbook.com/about-pitchbook pitchbook_output.txt
	java ExtractHTML https://www.youtube.com youtube_output.txt
	java ExtractHTML nltk.org/book/ch01.html nltk_output.txt
	java ExtractHTML https://github.com github_output.txt
	java ExtractHTML google.com google_output.txt
	java ExtractHTML https://www.reddit.com reddit_output.txt
	java ExtractHTML https://mywestern.wwu.edu wwu_output.txt
	java ExtractHTML https://en.wikipedia.org/wiki/Main_Page wiki_ouput.txt
	java ExtractHTML https://yahoo.com yahoo_output.txt
	@echo "Program finished."
