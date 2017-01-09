HTML Extraction Program

Author: Raymond Weiming Luo

----------------------------------------------------------------------------------------------
                                            FILES
----------------------------------------------------------------------------------------------
ExtractHTML.java - Main java file using to retrieve the HTML document of a website and finds 
                   all the links, HTML tags, and sequences of two or more words. The output
                   will be written into a text file in a newly created directory called
                   OutputTxtFiles.

HtmlData.java    - Object to save the HTML lines to easily store and access the data recieved 
                   from the HTML document.

Makefile         - Compiles, runs and cleans the Java program. The run contains 20 test cases 
                   used to test the program.
----------------------------------------------------------------------------------------------
                                           OVERVIEW
----------------------------------------------------------------------------------------------
The Java program recieves two arguments (args[0] = website URL | args[1] = output file name).
A URLConnection with the URL from args[0] will be established to recieve the input from the 
website to retrieve the HTML document. If the website URL given by args[0] does not have a 
transport protocol, use 'http'. The HTML document will be parsed to find all the links, HTML 
tags and sequences of two of more words (A word is classified as a series of characters with 
a whitespace seperator and the first letter is uppercase). After parsing the HTML document, 
the program will write the output to the file given by args[1]. All the output files will be 
stored in the directory OutputTxtFiles for convinience and organization. The OutputTxtFiles 
directory must be made beforehand, created by running the Makefile, or uncomment the 
section of the code in the main function of the ExtractHTML.java file.

NOTE: If given a particular HTTP website URL and the output text files show little or no data.
      Include the transport protocol of HTTPS to the website URL when passing it as an argument.
