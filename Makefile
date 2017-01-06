make:
	javac ExtractHTML.java

clean:
	$(RM) *.class , *~ , *# 

run:
	java ExtractHTML pitchbook.com/about-pitchbook output.txt
