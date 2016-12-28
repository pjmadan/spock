package com.spock.utils;

public class CharsUtil {
	 private static final String PLAIN_ASCII ="ÀàÈèIiOoÙùAaÉéIiOoUuYyÂâÊêÎîÔôÛûYyAaOoNnAaËëÏïOoUuYyAaCçOoUu";
//		      "AaEeIiOoUu"    // grave
//		    + "AaEeIiOoUuYy"  // acute
//		    + "AaEeIiOoUuYy"  // circumflex
//		    + "AaOoNn"        // tilde
//		    + "AaEeIiOoUuYy"  // umlaut
//		    + "Aa"            // ring
//		    + "Cc"            // cedilla
//		    + "OoUu"          // double acute
//		    ;

		  private static final String UNICODE =
		     "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
		    + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
		    + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
		    + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
		    + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
		    + "\u00C5\u00E5"
		    + "\u00C7\u00E7"
		    + "\u0150\u0151\u0170\u0171"
		    ;

		  // remove accentued from a string and replace with ascii equivalent
		  public static String convertNonAscii(String s) {
		    if (s == null) return null;
		      StringBuilder sb = new StringBuilder();
		      int n = s.length();
		      for (int i = 0; i < n; i++) {
		        char c = s.charAt(i);
		        int pos = UNICODE.indexOf(c);
		        if (pos > -1){
		          sb.append(PLAIN_ASCII.charAt(pos));
		        }
		        else {
		          sb.append(c);
		        }
		     }
		     return sb.toString();
		  }
	
		  public static String replaceLatinChars(String value){
			  if(value == null)
				  return value;
			  
			  return value.replace("\\u2018", "‘").replace("\\u2019", "’")
					  .replace("\\u00C1", "Á").replace("\\u00c1", "Á")
					  .replace("\\u00E1", "á").replace("\\u00e1", "á")
					  .replace("\\u00C9", "É").replace("\\u00c9", "É")
					  .replace("\\u00E9", "é").replace("\\u00e9", "é")
					  .replace("\\u00CD", "Í").replace("\\u00cd", "Í")
					  .replace("\\u00ED", "í").replace("\\u00ed", "í")
					  .replace("\\u00D3", "Ó").replace("\\u00d3", "Ó")
					  .replace("\\u00F3", "ó").replace("\\u00f3", "ó")
					  .replace("\\u00DA", "Ú").replace("\\u00da", "Ú")
					  .replace("\\u00FA", "ú").replace("\\u00fa", "ú")
					  .replace("\\u00D1", "Ñ").replace("\\u00d1", "Ñ")
					  .replace("\\u00F1", "ñ").replace("\\u00f1", "ñ")
					  .replace("\\u00A1", "¡").replace("\\u00a1", "¡")
					  .replace("\\u00BF", "¿").replace("\\u00bf", "¿")
					  .replace("\\u00A9", "©").replace("\\u00a9", "©");
		  }	 
		  
}
