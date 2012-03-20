package edu.harvard.hul.ois.pds;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Utils {
	
	public static String readFileAsString(String filePath) throws java.io.IOException {
		 StringBuffer fileData = new StringBuffer(1000);
		 BufferedReader reader = new BufferedReader(new FileReader(filePath));
		 char[] buf = new char[1024];
		 int numRead=0;
		 while((numRead=reader.read(buf)) != -1){
			 String readData = String.valueOf(buf, 0, numRead);
			 fileData.append(readData);
			 buf = new char[1024];
		 }
		 reader.close();
		 return fileData.toString();
	 }

	 public static String escapeHtml(String aText){
	     if(aText == null) {
	    	 return null;
	     }
	     final StringBuilder result = new StringBuilder();
	     final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	     char character =  iterator.current();
	     while (character != CharacterIterator.DONE ){
	       if (character == '<') {
	         result.append("&lt;");
	       }
	       else if (character == '>') {
	         result.append("&gt;");
	       }
	       else if (character == '&') {
	         result.append("&amp;");
	      }
	       else if (character == '\"') {
	         result.append("&quot;");
	       }
	       else if (character == '\'') {
	         result.append("&#039;");
	       }
	       else if (character == '(') {
	         result.append("&#040;");
	       }
	       else if (character == ')') {
	         result.append("&#041;");
	       }
	       else if (character == '#') {
	         result.append("&#035;");
	       }
	       else if (character == '%') {
	         result.append("&#037;");
	       }
	       else if (character == ';') {
	         result.append("&#059;");
	       }
	       else if (character == '+') {
	         result.append("&#043;");
	       }
	       else if (character == '-') {
	         result.append("&#045;");
	       }
	       else {
	         //the char is not a special one
	         //add it to the result as is
	         result.append(character);
	       }
	       character = iterator.next();
	     }
	     return result.toString();
	  }


}
