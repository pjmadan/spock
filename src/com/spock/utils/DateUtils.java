package com.spock.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
public class DateUtils {
	 private static final String TAG = "DateUtils";
	  
	  private static final SimpleDateFormat[] dateFormats;
	  private static final int defaultFormat = 2;
	  
	  private DateUtils() { }
	  
	  static {
	    final String[] possibleFormats = {
	      "EEE, dd MMM yyyy HH:mm:ss z", // RFC_822
	        "EEE, dd MMM yyyy HH:mm zzzz",
	        "yyyy-MM-dd HH:mm:ss",
	        "yyyy-MM-dd'T'HH:mm:ssZ",
	        "yyyy-MM-dd'T'HH:mm:ss.SSSzzzz", // Blogger Atom feed has millisecs also
	        "yyyy-MM-dd'T'HH:mm:sszzzz",
	        "yyyy-MM-dd'T'HH:mm:ss z",
	        "yyyy-MM-dd'T'HH:mm:ssz", // ISO_8601
	        "yyyy-MM-dd'T'HH:mm:ss",
	        "yyyy-MM-dd'T'HHmmss.SSSz",
	        "yyyy-MM-dd"
	    };
	    
	    dateFormats = new SimpleDateFormat[possibleFormats.length];
	      TimeZone gmtTZ = TimeZone.getTimeZone("GMT");

	      for (int i = 0; i < possibleFormats.length; i++)
	      {
	        /* TODO: Support other locales? */
	        dateFormats[i] = new SimpleDateFormat(possibleFormats[i],
	          Locale.ENGLISH);

	      //  dateFormats[i].setTimeZone(gmtTZ);
	      }
	  }
	  
	  public static Date parseDate(String str) {
	    Date result = null;
	    str = str.trim();
	    
	    if (str.length() > 10) {
	      // TODO deal with +4:00 (no zero before hour)
	      if ((str.substring(str.length() - 5).indexOf("+") == 0 || str
	          .substring(str.length() - 5).indexOf("-") == 0)
	          && str.substring(str.length() - 5).indexOf(":") == 2) {

	        String sign = str.substring(str.length() - 5,
	            str.length() - 4);

	        str = str.substring(0, str.length() - 5) + sign + "0"
	        + str.substring(str.length() - 4);

	      }

	      String dateEnd = str.substring(str.length() - 6);

	      // try to deal with -05:00 or +02:00 at end of date
	      // replace with -0500 or +0200
	      if ((dateEnd.indexOf("-") == 0 || dateEnd.indexOf("+") == 0)
	          && dateEnd.indexOf(":") == 3) {
	        // TODO deal with GMT-00:03
	        if ("GMT".equals(str.substring(str.length() - 9, str
	            .length() - 6))) {
	        } else {
	          // continue treatment
	          String oldDate = str;
	          String newEnd = dateEnd.substring(0, 3) + dateEnd.substring(4);
	          str = oldDate.substring(0, oldDate.length() - 6) + newEnd;
	        }
	      }
	    }
	    
	    int i = 0;
	    
	    while (i < dateFormats.length) {
	      try {
	        result = dateFormats[i].parse(str);
	        break;
	      } catch (java.text.ParseException eA) {
	        i++;
	      }
	    }

	    return result;
	  }
	  
	  /**
	   * Format a date in a manner that would be most suitable for serialized
	   * storage.
	   *
	   * @param date
	   *   {@link Date} object to format.
	   *
	   * @return
	   *   Robust, formatted date string.
	   */
	  public static String formatDate(Date date) {
	    return dateFormats[defaultFormat].format(date);
	  }
}
