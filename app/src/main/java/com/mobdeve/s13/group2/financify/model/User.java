package com.mobdeve.s13.group2.financify.model;

import java.util.ArrayList;

/**
 * This class embodies the user account data model for the "users" collection model
 * in Firebase Realtime Database
 */
public class User {
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
     * User's password
     */
    private String password;
    /**
     * User's list of accounts
     */
    private ArrayList<Account> accounts;
    /**
     * User's list of reminders
     */
    private ArrayList<Reminder> reminders;



    /**
     * Constructor. Instantiates all required attributes of the user
     *
     * @param   firstName   the user's first name
     * @param   lastName    the user's last name
     * @param   email       the user's email address
     * @param   password    the user's password
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.accounts = new ArrayList<Account>();
        this.reminders = new ArrayList<Reminder>();
    }

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
     * Retrieves the password of this user.
     *
     * @return  the user's password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Retrieves a copy of this user's list of accounts.
     *
     * @return  the copy of the user's list of accounts.
     */
    public ArrayList<Account> getAccounts() {
        return new ArrayList<> (accounts);
    }

    public ArrayList<Reminder> getReminders() {
        return new ArrayList<> (reminders);
    }
}
