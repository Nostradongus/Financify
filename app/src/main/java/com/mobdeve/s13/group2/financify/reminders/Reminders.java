package com.mobdeve.s13.group2.financify.reminders;

public class Reminders {
    // elements
    private String description, type, title, date;

    public Reminders(String description, String type, String title, String date) {
        this.description = description;
        this.type = type;
        this.title = title;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

}
