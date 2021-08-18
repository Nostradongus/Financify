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
    // A list of all the Transactions
    private ArrayList<Transaction> transactions;

    /**
     * Constructor. Instantiates the attributes of this class.
     *
     * @param data  a list of all the Transactions.
     */
    public TransactionAdapter (ArrayList<Transaction> data) {
        this.transactions = data;
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

        // TODO: Implement when UPDATE TRANSACTION is done.
        transViewHolder.getContainer ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent i = new Intent(view.getContext (), CashflowUpdateEntryActivity.class);

//                i.putExtra (Keys.KEY_ID, transactions.get (transViewHolder.getBindingAdapterPosition ()).getId ());
//                i.putExtra (Keys.KEY_NAME, transactions.get (transViewHolder.getBindingAdapterPosition ()).getName ());
//                i.putExtra (Keys.KEY_BAL, transactions.get (transViewHolder.getBindingAdapterPosition ()).getBalance());
//                i.putExtra (Keys.KEY_TYPE, transactions.get (transViewHolder.getBindingAdapterPosition ()).getType ());

                view.getContext ().startActivity (i);
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
