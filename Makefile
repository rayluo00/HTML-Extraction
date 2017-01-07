make:
	javac ExtractHTML.java HtmlData.java

clean:
	$(RM) *.class , *~ , *# 

run:
	java ExtractHTML pitchbook.com/about-pitchbook output.txt

run2:
	java ExtractHTML https://www.youtube.com output.txt
