package com.mobdeve.s13.group2.financify.helpers;

import java.util.Calendar;

/**
 * Helper class for any date-related functionalities in Financify.
 */
public class DateHelper {
    /**
     * Retrieve the date today in MM/DD/YYYY format.
     *
     * @return  the date today
     */
    public static String getDateToday () {
        // Get date today
        Calendar calendar = Calendar.getInstance ();
        int year = calendar.get (Calendar.YEAR);
        int month = calendar.get (Calendar.MONTH);
        month = month + 1;
        int day = calendar.get (Calendar.DAY_OF_MONTH);

        // Return formatted date
        return month + "/" + day + "/" + year;
    }

    /**
     * Returns a date in Month Day, Year format.
     *
     * @param day       day value
     * @param month     month value
     * @param year      year value
     *
     * @return  formatted date
     */
    public static String makeDateString (int day, int month, int year) {
        return getMonthFormat (month) + " " + day + ", " + year;
    }

    /**
     * Returns a month (i.e., January) given an integer corresponding
     * to the month itself.
     *
     * @param month the month to be converted into String
     *
     * @return  a String version of the month
     */
    public static String getMonthFormat (int month) {
        String[] months = new String[] {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        // If within range
        if (month > 0 && month < 12)
            return months[month - 1];

        // Default
        return "January";
    }

    /**
     * Checks if a given date in format MM/DD/YYYY is in the past.
     *
     * @param date the date to be checked
     * @return  true if the date is the past; otherwise false
     */
    public static boolean isDateInPast (String date) {
        // separate month, day, and year values and parse them
        String[] dateVals = date.split ("/");
        int month = Integer.parseInt (dateVals[0]) - 1;
        int day = Integer.parseInt (dateVals[1]);
        int year = Integer.parseInt (dateVals[2]);

        // Retrieve date & time today
        Calendar cal = Calendar.getInstance ();

        // Check if date is in past
        if (year < cal.get (Calendar.YEAR))
            return true;
        else if (year == cal.get (Calendar.YEAR) &&
                month < cal.get (Calendar.MONTH))
            return true;
        else if (year == cal.get (Calendar.YEAR) &&
                month == cal.get (Calendar.MONTH) &&
                day < cal.get (Calendar.DAY_OF_MONTH))
            return true;
        else
            return false;
    }
}
