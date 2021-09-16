package com.mobdeve.s13.group2.financify.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class embodies the reminders a user creates in the Reminder feature
 * of Financify.
 */
public class Reminder implements Parcelable{

    // Types of reminders [Urgent or Non-urgent]
    public static final String TYPE_URGENT = "Urgent";
    public static final String TYPE_NON = "Nonurgent";

    // ID of the reminder
    private final String ID;
    // Title of the reminder
    private String title;
    // Short description
    private String desc;
    // Type of the reminder
    private String type;
    // Date/ deadline of the reminder for notification
    private String date;

    /**
     * Constructor. Instantiates all of the attributes of Reminder
     * @param id
     * @param title
     * @param desc
     * @param type
     * @param date
     */
    public Reminder(String id, String title, String desc, String type, String date) {
        this.ID = id;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.date = date;
    }

    // GETTERS

    /**
     * Retrieves the ID of this Reminder.
     *
     * @return  the ID of this Reminder.
     */
    public String getId () {
        return this.ID;
    }

    /**
     * Retrieves the title of this Reminder.
     *
     * @return  the name of this Reminder.
     */
    public String getTitle () {
        return this.title;
    }

    /**
     * Retrieves the description of this Reminder.
     *
     * @return  the desc of this Reminder.
     */
    public String getDesc () {
        return this.desc;
    }

    /**
     * Retrieves the type of this Reminder.
     *
     * @return  the type of this Reminder.
     */
    public String getType () {
        return this.type;
    }

    /**
     * Retrieves the date of this Transaction.
     *
     * @return  the date of this Transaction.
     */
    public String getDate () {
        return this.date;
    }

    /**
     * Retrieves the month of the date of this Reminder.
     *
     * @return  the month of this Reminder.
     */
    public String getMonth () {
        String[] temp = this.date.split ("/");

        return temp[0];
    }

    /**
     * Retrieves the year of the date of this Reminder.
     *
     * @return  the year of this Reminder.
     */
    public String getYear () {
        String[] temp = this.date.split ("/");

        return temp[2];
    }

    // SETTERS

    /**
     * Sets the title of this Reminder.
     *
     * @param   title    the new title for this Reminder.
     */
    public void setTitle (String title) {
        this.title = title;
    }

    /**
     * Sets the description of this Reminder.
     *
     * @param   desc    the new description for this Reminder.
     */
    public void setDesc (String desc) {
        this.desc = desc;
    }


    /**
     * Sets the type of this Reminder.
     *
     * @param   type    the new type of this Reminder.
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Sets the date of this Reminder.
     *
     * @param   date    the new date of this Reminder.
     */
    public void setDate (String date) {
        this.date = date;
    }

    /**
     * Creator constant. Used for Parcelable.
     */
    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
        @Override
        public Reminder createFromParcel (Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray (int size) {
            return new Reminder[size];
        }
    };

    /**
     * Constructor for Parcelable.
     */
    protected Reminder (Parcel in) {
        ID = in.readString ();
        title = in.readString ();
        desc = in.readString ();
        type = in.readString ();
        date = in.readString ();
    }

    /**
     * For Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * For Parcelable.
     */
    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString (ID);
        dest.writeString (title);
        dest.writeString (desc);
        dest.writeString (type);
        dest.writeString (date);
    }
}
