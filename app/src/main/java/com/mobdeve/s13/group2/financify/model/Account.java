package com.mobdeve.s13.group2.financify.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * This class embodies the accounts a user creates in the Cash Flow feature
 * of Financify.
 */
public class Account implements Parcelable {
    // Account types
    /**
     * "Bank Account" Transaction type.
     */
    public static final String TYPE_BANK = "Bank Account";
    /**
     * "Digital Wallet" Transaction type.
     */
    public static final String TYPE_DIGITAL = "Digital Wallet";
    /**
     * "Physical Wallet" Transaction type.
     */
    public static final String TYPE_PHYSICAL = "Physical Wallet";

    /**
     * Unique ID of the Account.
     */
    private final String ID;
    /**
     * Name of the Account.
     */
    private String name;
    /**
     * Current balance of the Account.
     */
    private double balance;
    /**
     * Type of the Account.
     */
    private String type;
    /**
     * Transactions under this Account.
     */
    private ArrayList<Transaction> transactions;

    /**
     * Constructor. Instantiates all of the attributes of this class.
     *
     * @param   id      the id of this Account.
     * @param   name    the name of this Account.
     * @param   balance the current balance of this Account.
     * @param   type    the type of this Account.
     */
    public Account (String id, String name, double balance, String type) {
        this.ID = id;
        this.name = name;
        this.balance = balance;
        this.type = type;
        this.transactions = new ArrayList<> ();
    }

    /**
     * Constructor. Instantiates all of the attributes of this class.
     *
     * @param   id              the id of this Account.
     * @param   name            the name of this Account.
     * @param   balance         the current balance of this Account.
     * @param   type            the type of this Account.
     * @param   transactions    a list of Transactions under this Account.
     */
    public Account (String id, String name, double balance, String type, ArrayList<Transaction> transactions) {
        this.ID = id;
        this.name = name;
        this.balance = balance;
        this.type = type;
        this.transactions = new ArrayList<> (transactions);

//        updateBalance ();
    }

    /**
     * Retrieves the ID of this Account.
     *
     * @return  the ID of this Account.
     */
    public String getId () {
        return this.ID;
    }

    /**
     * Retrieves the name of this Account.
     *
     * @return  the name of this Account.
     */
    public String getName () {
        return this.name;
    }

    /**
     * Retrieves the balance of this Account.
     *
     * @return  the balance of this Account.
     */
    public double getBalance () {
        return this.balance;
    }

    /**
     * Retrieves the balance of this Account in currency format.
     *
     * @return  the balance of this Account in currency format.
     */
    public String getBalanceFormatted () {
        return NumberFormat.getCurrencyInstance ().format (this.balance);
    }

    /**
     * Retrieves the type of this Account.
     *
     * @return  the type of this Account.
     */
    public String getType () {
        return this.type;
    }

    /**
     * Retrieves a copy of this Account's list of Transactions.
     *
     * @return  a list of Transactions.
     */
    public ArrayList<Transaction> getTransactions () {
        return new ArrayList<> (transactions);
    }

    /**
     * Retrieves a specific Transaction, given a Transaction id.
     *
     * @param   id  the id of the Transaction to be retrieved
     * @return      a copy of the found Transaction object
     */
    public Transaction getTransaction (String id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId ().equalsIgnoreCase (id)) {
                return new Transaction (
                        transaction.getId (),
                        transaction.getDescription (),
                        transaction.getAmount (),
                        transaction.getType (),
                        transaction.getDate (),
                        transaction.getAccountId ()
                );
            }
        }

        return null;
    }

    /**
     * Retrieves the latest Transaction.
     *
     * @return  a copy of the latest Transaction
     */
    public Transaction getLatestTransaction () {
        if (transactions.size () > 0) {
            Transaction temp = this.transactions.get(transactions.size() - 1);
            return new Transaction (
                    temp.getId (),
                    temp.getDescription (),
                    temp.getAmount (),
                    temp.getType (),
                    temp.getDate (),
                    temp.getAccountId ()
            );
        } else {
            return null;
        }
    }

    /**
     * Sets the name of this Account.
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Sets the balance of this Account.
     */
    public void setBalance (double bal) {
        this.balance = bal;
    }

    /**
     * Sets the type of this Account.
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Empties the list of Transactions of this Account.
     */
    public void emptyTransactions () {
        this.transactions.clear ();
    }

    /**
     * Updates the whole list of Transactions of this Account.
     *
     * @param newList   the new list of Transactions.
     */
    public void setTransactions (ArrayList<Transaction> newList) {
        this.transactions = new ArrayList<> (newList);
    }

    /**
     * Retrieves the amount of Transactions under this Account.
     */
    public int getTransactionCount () {
        return this.transactions.size ();
    }

    /**
     * Adds a new Transaction to this Account.
     *
     * @param   desc    the description of the Transaction.
     * @param   amt     the amount affiliated with the Transaction.
     * @param   type    the type of the Transaction.
     * @param   date    the date of the Transaction.
     */
    public void addTransaction (String desc, double amt, String type, String date) {
        this.transactions.add (
                new Transaction (
                        this.getNewTransactionId (),
                        desc,
                        amt,
                        type,
                        date,
                        this.ID
                )
        );

        updateAddBalance ();
    }

    /**
     * Updates a Transaction in this Account.
     *
     * @param   id      the id of the Transaction to be updated.
     * @param   desc    the new description of the Transaction.
     * @param   amt     the new amount of the Transaction.
     * @param   type    the new type of the Transaction.
     * @param   date    the new date of the Transaction.
     *
     * @return  true if the Transaction has been updated successfully, otherwise false.
     */
    public boolean updateTransaction (String id, String desc, double amt, String type, String date) {
        // retrieve index first
        int index = getIdIndex (id);

        // if existing Transaction
        if (index != -1) {
            // Remove amount from total balance
            updateRemoveBalance (index);

            // Update Transaction
            this.transactions.set (
                    index,
                    new Transaction (
                            id,
                            desc,
                            amt,
                            type,
                            date,
                            this.ID
                    )
            );

            // Add amount to total balance
            updateAddBalance (index);

            return true;
        // if non-existent Transaction
        } else {
            return false;
        }

    }

    /**
     * Removes a Transaction from this Account.
     *
     * @param   id  the id of the Transaction to be removed.
     *
     * @return  true if the Transaction has been removed successfully, otherwise false.
     */
    public boolean removeTransaction (String id) {
        // retrieve index first
        int index = getIdIndex (id);

        // if existing Transaction
        if (index != -1) {
            updateRemoveBalance (index);

            // remove
            this.transactions.remove (index);

            return true;
            // if non-existent Transaction
        } else {
            return false;
        }
    }

    /**
     * Updates the balance of this Account when a new Transaction is added to it.
     */
    private void updateAddBalance () {
        double bal = this.balance;
        Transaction transaction = getLatestTransaction();

        if (transaction.getType ().equalsIgnoreCase (Transaction.TYPE_INCOME))
            bal += transaction.getAmount();
        else
            bal -= transaction.getAmount();

        balance = bal;
    }

    /**
     * Updates the balance of this Account when one of its Transactions is updated.
     *
     * @param index the index of the Transaction that was updated
     */
    private void updateAddBalance (int index) {
        double bal = this.balance;
        Transaction transaction = transactions. get (index);

        if (transaction.getType ().equalsIgnoreCase (Transaction.TYPE_INCOME))
            bal += transaction.getAmount();
        else
            bal -= transaction.getAmount();

        balance = bal;
    }

    /**
     * Updates the balance of this Account when a Transaction is removed from it.
     */
    private void updateRemoveBalance (int index) {
        double bal = this.balance;
        Transaction transaction = transactions.get (index);

        if (!transaction.getType ().equalsIgnoreCase (Transaction.TYPE_INCOME))
            bal += transaction.getAmount();
        else
            bal -= transaction.getAmount();

        balance = bal;
    }

    /**
     * Retrieves a new Transaction id for a new Transaction.
     *
     * @return  an integer corresponding to a new Transaction id.
     */
    private String getNewTransactionId () {
        // if there are Transactions already
        if (this.transactions.size () > 0) {
            // get the latest Transaction id, increment, then return it
            return String.valueOf ((Integer.parseInt (transactions.get (transactions.size () - 1).getId ()) + 1));
        // if there are no Transactions
        } else
            // return smallest possible ID
            return "0";
    }

    /**
     * Retrieves the index of a Transaction given a Transaction ID.
     *
     * @param   id  the ID of a Transaction that is being searched for.
     *
     * @return  an integer corresponding to the index of a Transaction.
     */
    private int getIdIndex (String id) {
        // traverse list of Transactions
        for (int i = 0; i < transactions.size (); i++) {
            // if Transaction is found based on ID
            if (transactions.get (i).getId ().equalsIgnoreCase (id))
                return i;
        }

        // if no Transaction was found
        return -1;
    }

    /**
     * Creator constant. Used for Parcelable.
     */
    public static final Creator<Account> CREATOR = new Creator<Account> () {
        @Override
        public Account createFromParcel (Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray (int size) {
            return new Account[size];
        }
    };

    /**
     * Constructor for Parcelable.
     */
    protected Account (Parcel in) {
        ID = in.readString ();
        name = in.readString ();
        balance = in.readDouble ();
        type = in.readString ();
        transactions = in.createTypedArrayList (Transaction.CREATOR);
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
        dest.writeString (name);
        dest.writeDouble (balance);
        dest.writeString (type);
        dest.writeTypedList (transactions);
    }
}
