package com.mobdeve.s13.group2.financify.cashflow;

/**
 * This serves as the class representation of a Transaction in the application.
 */
public class Transaction {
    // Static constants for Transaction types
    public static final String TYPE_INCOME = "Income";
    public static final String TYPE_EXPENSE = "Expense";
    public static final String TYPE_INVESTMENT = "Investment";

    // Attributes
    // Description of Transaction
    private String description;
    // Amount affiliated with Transaction
    private float amount;
    // Type of Transaction
    private String type;
    // Date of Transaction
    private String date;

    /**
     * Constructor. Instantiates the attributes of this class.
     *
     * @param desc   the description of this Transaction
     * @param amt    the amount affiliated with this Transaction
     * @param type   the type of this Transaction
     * @param date   the date of this Transaction
     */
    public Transaction (String desc, float amt, String type, String date) {
        this.description = desc;
        this.amount = amt;
        this.type = type;
        this.date = date;
    }

    /**
     * Returns the description of this Transaction.
     *
     * @return  the description of this Transaction
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Returns the amount affiliated with this Transaction.
     *
     * @return  the amount affilaited with this Transaction
     */
    public float getAmount () {
        return this.amount;
    }

    /**
     * Returns the type of this Transaction.
     *
     * @return  the type of this Transaction
     */
    public String getType () {
        return this.type;
    }

    /**
     * Returns the date of this Transaction.
     *
     * @return  the date of this Transaction
     */
    public String getDate () {
        return this.date;
    }

    /**
     * Returns the month of the date of this Transaction.
     *
     * @return the month of the date of this Transaction
     */
    public String getMonth () {
        String[] temp = this.date.split ("/");

        return temp[0];
    }

    /**
     * Returns the year of the date of this Transaction.
     *
     * @return the year of the date of this Transaction
     */
    public String getYear () {
        String[] temp = this.date.split ("/");

        return temp[2];
    }

    /**
     * Sets the description of this Transaction.
     *
     * @param desc  the description of this Transaction
     */
    public void setDescription (String desc) {
        this.description = desc;
    }

    /**
     * Sets the amount affiliated with this Transaction.
     *
     * @param amt the amount affiliated with this Transaction
     */
    public void setAmount (float amt) {
        this.amount = amt;
    }

    /**
     * Sets the type of this Transaction.
     *
     * @param type  the type of this Transaction
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Sets the date of this Transaction.
     *
     * @param date  the date of this Transaction
     */
    public void setDate (String date) {
        this.date = date;
    }
}
