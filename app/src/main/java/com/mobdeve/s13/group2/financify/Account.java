package com.mobdeve.s13.group2.financify;


/**
 * This serves as the class representation of an Account in the application.
 */
public class Account {
    // Static constants for Account types
    public static final String TYPE_BANK = "Bank Account";
    public static final String TYPE_DIGITAL = "Digital Wallet";
    public static final String TYPE_PHYSICAL = "Physical Wallet";

    // Attributes
    // ID of the Account
    private String id;
    // Name of the Account
    private String name;
    // Balance of the Account
    private float balance;
    // Type of the Account
    private String type;

    /**
     * Constructor. Instantiates the attributes of this class.
     *
     * @param id    the id of this Account
     * @param name  the name of this Account
     * @param bal   the balance of this Account
     * @param type  the type of this Account
     */
    public Account (String id, String name, int bal, String type) {
        this.id = id;
        this.name = name;
        this.balance = bal;
        this.type = type;
    }

    /**
     * Returns the ID of this Account.
     *
     * @return the ID of this Account
     */
    public String getId () {
        return this.id;
    }

    /**
     * Returns the name of this Account.
     *
     * @return  the name of this Account
     */
    public String getName () {
        return this.name;
    }

    /**
     * Returns the balance of this Account.
     *
     * @return  the balance of this Account
     */
    public float getBalance () {
        return this.balance;
    }

    /**
     * Returns the type of this Account
     *
     * @return  the type of this Account
     */
    public String getType () {
        return this.type;
    }

    /**
     * Sets the ID of this Account
     *
     * @param id the ID of this Account
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * Sets the name of this Account.
     *
     * @param name  the name of this Account
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Sets the balance of this Account.
     *
     * @param bal   the balance of this Account
     */
    public void setBalance (float bal) {
        this.balance = bal;
    }

    /**
     * Sets the type of this Account.
     *
     * @param type  the type of this Account
     */
    public void setType (String type) {
        this.type = type;
    }
}
