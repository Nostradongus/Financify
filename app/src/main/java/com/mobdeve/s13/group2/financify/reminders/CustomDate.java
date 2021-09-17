package com.mobdeve.s13.group2.financify.reminders;


import java.util.Calendar;

/**
 * Class for creating and formatting dates with self-customized format.
 */
public class CustomDate {
    // array of shortened month names
    private static final String[] monthString = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    // date values
    private int day_in_month, month, year;

    /**
     * Default constructor, creates today's date.
     */
    public CustomDate () {
        Calendar c = Calendar.getInstance();

        this.year = c.get(Calendar.YEAR);
        this.day_in_month = c.get(Calendar.DAY_OF_MONTH);
        this.month = c.get(Calendar.MONTH);
    }

    /**
     * Constructor for creating specified date.
     *
     * @param year  year value specified
     * @param month month value specified
     * @param day   day value specified
     */
    public CustomDate(int year, int month, int day) {
        this.year = year;
        this.day_in_month = day;
        this.month = month;
    }

    public String toStringFull() {
        return monthString[month] + " " + this.day_in_month + ", " + this.year;
    }
}
