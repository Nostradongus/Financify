package com.mobdeve.s13.group2.financify.reminders;

import java.util.ArrayList;
import java.util.Collections;

class DataHelper {
    public ArrayList<Reminders> initializeData() {
        String[] types = {"Urgent", "Not Urgent"};

        ArrayList<Reminders> data = new ArrayList<>();
        data.add(new Reminders(
                "Utility Bills for the month of January",
                types[0],
                "January Utility Bills",
                "February 14, 2021"
        ));
        data.add(new Reminders(
                "Expenses for the month of February",
                types[0],
                "February Deadline",
                "March 14, 2021"
        ));
        data.add(new Reminders(
                "Credit cards payment deadline",
                types[0],
                "Credit Card Deadline",
                "September 1, 2021"
        ));

        Collections.shuffle(data);

        return data;
    }
}

