make:
	javac ExtractHTML.java HtmlData.java

clean:
	$(RM) *.class , *~ , *# 

run:
	@echo "Running program...\n"
	java ExtractHTML pitchbook.com/about-pitchbook pitchbook_output.txt
	java ExtractHTML https://www.youtube.com youtube_output.txt
	java ExtractHTML nltk.org/book/ch01.html nltk_output.txt
	java ExtractHTML https://github.com github_output.txt
	java ExtractHTML google.com google_output.txt
	@echo "Program finished."
