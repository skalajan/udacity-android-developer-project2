package com.example.android.popularmovies.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Basic class with common function used inside the app.
 */
public class CommonUtils {
    /**
     * Parses the date string received from the server.
     * @param dateString Date string in format 'yyyy-MM-dd'
     * @return Parsed Date
     * @throws ParseException Thrown when could not parse the string.
     */
    public static Date parseDateFromReceivedString(String dateString) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return df.parse(dateString);
    }

    /**
     * Formats the date into string in 'dd.MM.yyyy' format.
     * @param date Date to be parsed.
     * @return Date string.
     */
    public static String formatDateToHumanReadableString(Date date){
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        return df.format(date);
    }
}
