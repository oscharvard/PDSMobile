package edu.harvard.hul.ois.pds;

import java.util.*;

public class UnescapeHtml {

  private UnescapeHtml() {}
  
  private static HashMap<String,String> htmlEntities;
  static {
    htmlEntities = new HashMap<String,String>();
    htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
    htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
    htmlEntities.put("&agrave;",""); htmlEntities.put("&Agrave;","");
    htmlEntities.put("&acirc;","") ; htmlEntities.put("&auml;","");
    htmlEntities.put("&Auml;","")  ; htmlEntities.put("&Acirc;","");
    htmlEntities.put("&aring;","") ; htmlEntities.put("&Aring;","");
    htmlEntities.put("&aelig;","") ; htmlEntities.put("&AElig;","" );
    htmlEntities.put("&ccedil;",""); htmlEntities.put("&Ccedil;","");
    htmlEntities.put("&eacute;",""); htmlEntities.put("&Eacute;","" );
    htmlEntities.put("&egrave;",""); htmlEntities.put("&Egrave;","");
    htmlEntities.put("&ecirc;","") ; htmlEntities.put("&Ecirc;","");
    htmlEntities.put("&euml;","")  ; htmlEntities.put("&Euml;","");
    htmlEntities.put("&iuml;","")  ; htmlEntities.put("&Iuml;","");
    htmlEntities.put("&ocirc;","") ; htmlEntities.put("&Ocirc;","");
    htmlEntities.put("&ouml;","")  ; htmlEntities.put("&Ouml;","");
    htmlEntities.put("&oslash;","") ; htmlEntities.put("&Oslash;","");
    htmlEntities.put("&szlig;","") ; htmlEntities.put("&ugrave;","");
    htmlEntities.put("&Ugrave;",""); htmlEntities.put("&ucirc;","");
    htmlEntities.put("&Ucirc;","") ; htmlEntities.put("&uuml;","");
    htmlEntities.put("&Uuml;","")  ; htmlEntities.put("&nbsp;"," ");
    htmlEntities.put("&copy;","\u00a9");
    htmlEntities.put("&reg;","\u00ae");
    htmlEntities.put("&euro;","\u20a0");
    
    htmlEntities.put("&#039;","'");
    htmlEntities.put("&#040;","(");
    htmlEntities.put("&#041;",")");
    htmlEntities.put("&#035;","#");
    htmlEntities.put("&#037;","%");
    htmlEntities.put("&#059;",";");
    htmlEntities.put("&#043;","+");
    htmlEntities.put("&#045;","-");
    
  }

  public static final String unescape(String source, int start){
     int i,j;

     i = source.indexOf("&", start);
     if (i > -1) {
        j = source.indexOf(";" ,i);
        if (j > i) {
           String entityToLookFor = source.substring(i , j + 1);
           String value = (String)htmlEntities.get(entityToLookFor);
           if (value != null) {
             source = new StringBuffer().append(source.substring(0 , i))
                                   .append(value)
                                   .append(source.substring(j + 1))
                                   .toString();
             return unescape(source, i + 1); // recursive call
           }
         }
     }
     return source;
  }

  public static void main(String args[]) throws Exception {
      // to see accented character to the console
      java.io.PrintStream ps = new java.io.PrintStream(System.out, true, "Cp850");
      String test = "&copy; 2007  R&eacute;al Gagnon &lt;www.rgagnon.com&gt;";
      ps.println(test + "\n-->\n" +unescape(test, 0));

      /*
         output :
         &copy; 2007  R&eacute;al Gagnon &lt;www.rgagnon.com&gt;
         -->
          2007  Ral Gagnon <www.rgagnon.com>
      */
  }
}
