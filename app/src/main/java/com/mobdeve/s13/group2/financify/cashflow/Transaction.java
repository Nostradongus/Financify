package com.mobdeve.s13.group2.financify.cashflow;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class embodies the transactions that a user will relate to their accounts.
 */
public class Transaction implements Parcelable {
    // Transaction types
    /**
     * "Income" Transaction type.
     */
    public static final String TYPE_INCOME = "Income";
    /**
     * "Expense" Transaction type.
     */
    public static final String TYPE_EXPENSE = "Expense";
    /**
     * "Investment" Transaction type.
     */
    public static final String TYPE_INVESTMENT = "Investment";

    /**
     * Unique ID of the Transaction.
     */
    private final String ID;
    /**
     * Description of the Transaction
     */
    private String description;
    /**
     * Amount affiliated with the Transaction
     */
    private double amount;
    /**
     * Type of the Transaction
     */
    private String type;
    /**
     * Date of the Transaction
     */
    private String date;
    /**
     * ID of the Account where this Transaction belongs.
     */
    private final String ACCOUNT_ID;

    /**
     * Default Constructor.
     */
    public Transaction () {
        this.ID = "0";
        this.description = "";
        this.amount = 0;
        this.type = TYPE_INCOME;
        this.date = "01/01/1900";
        this.ACCOUNT_ID = "0";
    }

    /**
     * Constructor. Instantiates all of the attributes of this class.
     *
     * @param   id          the id of this Transaction.
     * @param   desc        the description of this Transaction.
     * @param   amt         the amount affiliated with this Transaction.
     * @param   type        the type of this Transaction.
     * @param   date        the date of this Transaction.
     * @param   accountId   the ID of the Account where this Transaction belongs.
     */
    public Transaction (String id, String desc, double amt, String type, String date, String accountId) {
        this.ID = id;
        this.description = desc;
        this.amount = amt;
        this.type = type;
        this.date = date;
        this.ACCOUNT_ID = accountId;
    }

    /**
     * Retrieves the ID of this Transaction.
     *
     * @return  the ID of this Transaction.
     */
    public String getId () {
        return this.ID;
    }

    /**
     * Retrieves the description of this Transaction.
     *
     * @return  the description of this Transaction.
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Retrieves the amount affiliated with this Transaction.
     *
     * @return  the amount affiliated with this Transaction.
     */
    public double getAmount () {
        return this.amount;
    }

    /**
     * Retrieves the type of this Transaction.
     *
     * @return  the type of this Transaction.
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
     * Retrieves the month of the date of this Transaction.
     *
     * @return  the month of this Transaction.
     */
    public String getMonth () {
        String[] temp = this.date.split ("/");

        return temp[0];
    }

    /**
     * Retrieves the year of the date of this Transaction.
     *
     * @return  the year of this Transaction.
     */
    public String getYear () {
        String[] temp = this.date.split ("/");

        return temp[2];
    }

    /**
     * Sets the description of this Transaction.
     *
     * @param   desc    the new description for this Transaction.
     */
    public void setDescription (String desc) {
        this.description = desc;
    }

    /**
     * Sets the amount affiliated with this Transaction.
     *
     * @param   amt     the new amount for this Transaction.
     */
    public void setAmount (double amt) {
        this.amount = amt;
    }

    /**
     * Sets the type of this Transaction.
     *
     * @param   type    the new type of this Transaction.
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Sets the date of this Transaction.
     *
     * @param   date    the new date of this Transaction.
     */
    public void setDate (String date) {
        this.date = date;
    }

    /**
     * Retrieves the ID of the Account this Transaction belongs to.
     *
     * @return  the ID of the Account this Transaction belongs to.
     */
    public String getAccountId () {
        return this.ACCOUNT_ID;
    }

    /**
     * Constructor for Parcelable.
     */
    protected Transaction (Parcel in) {
        ID = in.readString ();
        description = in.readString ();
        amount = in.readDouble ();
        type = in.readString ();
        date = in.readString ();
        ACCOUNT_ID = in.readString ();
    }

    /**
     * Used for Parcelable.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString (ID);
        dest.writeString (description);
        dest.writeDouble (amount);
        dest.writeString (type);
        dest.writeString (date);
        dest.writeString (ACCOUNT_ID);
    }

    /**
     * Used for Parcelable.
     */
    @Override
    public int describeContents () {
        return 0;
    }

    /**
     * Creator constant. Used for Parcelable.
     */
    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
