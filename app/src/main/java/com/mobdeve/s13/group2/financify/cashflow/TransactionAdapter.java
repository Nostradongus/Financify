package com.mobdeve.s13.group2.financify.cashflow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group2.financify.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Adapter for the Transaction entries that will be displayed in the Transactions RecyclerView.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
    // Account affiliated with Transaction
    private Account account;
    // List of al Transactions under this account
    private ArrayList<Transaction> transactions;

    /**
     * Constructor. Instantiates the attributes of this class.
     *
     * @param account       the Account affiliated with the Transaction
     * @param transactions  a list of all the Transactions.
     */
    public TransactionAdapter (Account account, ArrayList<Transaction> transactions) {
        this.account = account;
        this.transactions = transactions;
    }

    /**
     * Prepare numerous important RecyclerView elements.
     */
    @NonNull
    @NotNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Inflate cashflow_account_entry.xml here
        LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
        View itemView = inflater.inflate (R.layout.cashflow_transaction_entry, parent, false);

        TransactionViewHolder transViewHolder = new TransactionViewHolder (itemView);

        transViewHolder.getContainer ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent i = new Intent (view.getContext (), CashflowUpdateEntryActivity.class);

                i.putExtra (Keys.KEY_ACC, account);
                i.putExtra (Keys.KEY_TRAN, account.getTransaction (transactions.get (transViewHolder.getBindingAdapterPosition ()).getId ()));

                view.getContext ().startActivity (i);
                ((CashflowAccountActivity) view.getContext ()).finish ();
            }
        });

        return transViewHolder;
    }

    /**
     * Set TextView texts when a ViewHolder is bound.
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull TransactionViewHolder holder, int position) {
        Transaction currTransaction = transactions.get (position);

        holder.setTransDesc (currTransaction.getDescription ());
        holder.setTransAmt (currTransaction.getAmount ());
        holder.setTransType (currTransaction.getType ());
        holder.setTransDate (currTransaction.getDate ());
    }

    /**
     * Returns the size of the list of Transactions.
     *
     * @return the size of the list of Transactions
     */
    @Override
    public int getItemCount() {
        return this.transactions.size ();
    }
}
