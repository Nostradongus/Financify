package com.mobdeve.s13.group2.financify.reminders;


import java.util.Calendar;

public class CustomDate {
    private static final String[] monthString = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private int day_in_month, month, year;

    public CustomDate () {
        Calendar c = Calendar.getInstance();

        this.year = c.get(Calendar.YEAR);
        this.day_in_month = c.get(Calendar.DAY_OF_MONTH);
        this.month = c.get(Calendar.MONTH);
    }

    public CustomDate(int year, int month, int day) {
        this.year = year;
        this.day_in_month = day;
        this.month = month;
    }

    public String toStringFull() {
        return monthString[month] + " " + this.day_in_month + ", " + this.year;
    }
}
