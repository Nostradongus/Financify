package com.mobdeve.s13.group2.financify.cashflow;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.mobdeve.s13.group2.financify.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DateHelper {
    public static String getDateToday () {
        Calendar calendar = Calendar.getInstance ();
        int year = calendar.get (Calendar.YEAR);
        int month = calendar.get (Calendar.MONTH);
        month = month + 1;
        int day = calendar.get (Calendar.DAY_OF_MONTH);

        return makeDateString (day, month, year);
    }

    public static String makeDateString (int day, int month, int year) {
        return getMonthFormat (month) + " " + day + " " + year;
    }

    public static String getMonthFormat (int month) {
        String[] months = new String[] {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        if (month > 0 && month < 12)
            return months[month - 1];

        return "JAN";
    }
}
