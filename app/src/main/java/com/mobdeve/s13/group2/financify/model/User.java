package com.mobdeve.s13.group2.financify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This class embodies the user account data model for the "users" collection model
 * in Firebase Realtime Database
 */
public class User implements Parcelable {
    /**
     * User's first name
     */
    private String firstName;
    /**
     * User's last name
     */
    private String lastName;
    /**
     * User's email address
     */
    private String email;
    /**
     * User's list of accounts
     */
    private ArrayList<Account> accounts;
    /**
     * User's list of reminders
     */
    private ArrayList<Reminder> reminders;
    /**
     * User's PIN
     */
    private String PIN;
    /**
     * User preference. If user prefers to use device biometrics.
     */
    private boolean useBiometrics;

    /**
     * Constructor. Instantiates all required attributes of the user
     *
     * @param   firstName   the user's first name
     * @param   lastName    the user's last name
     * @param   email       the user's email address
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<Account>();
        this.reminders = new ArrayList<Reminder>();
        this.useBiometrics = false;
    }

    /**
     * For Parcelable.
     */
    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        accounts = in.createTypedArrayList(Account.CREATOR);
        reminders = in.createTypedArrayList(Reminder.CREATOR);
    }

    /**
     * For Parcelable.
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Retrieves the first name of this user.
     *
     * @return  the user's first name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Retrieves the last name of this user.
     *
     * @return  the user's last name.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Retrieves the email of this user.
     *
     * @return  the user's email address.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Retrieves a copy of this user's list of accounts.
     *
     * @return  the copy of the user's list of accounts.
     */
    public ArrayList<Account> getAccounts() {
        return new ArrayList<> (accounts);
    }

    /**
     * Retrieves a copy of this user's list of reminders.
     *
     * @return  the copy of the user's list of reminders
     */
    public ArrayList<Reminder> getReminders() {
        return new ArrayList<> (reminders);
    }

    /**
     * Retrieves the user's PIN.
     *
     * @return  the user's PIN.
     */
    public String getPIN () {
        return this.PIN;
    }

    /**
     * Sets the user's PIN.
     */
    public void setUserPIN (String PIN) {
        this.PIN = PIN;
    }

    /**
     * Retrieves the user's biometrics preference.
     *
     * @return  the biometrics preference
     */
    public boolean getUseBiometrics () {
        return this.useBiometrics;
    }

    /**
     * Sets the user's biometrics preference.
     */
    public void setUseBiometrics (boolean b) {
        this.useBiometrics = b;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeTypedList(accounts);
        dest.writeTypedList(reminders);
    }
}
