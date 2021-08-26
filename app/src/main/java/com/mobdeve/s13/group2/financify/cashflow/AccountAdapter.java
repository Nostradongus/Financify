package com.mobdeve.s13.group2.financify.cashflow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Adapter for the Account entries that will be displayed in the Accounts RecyclerView.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountViewHolder> {
    // A list of all the Transactions
    private ArrayList<Account> accounts;

    /**
     * Constructor. Instantiates the attributes of this class.
     *
     * @param data  a list of all the Accounts.
     */
    public AccountAdapter (ArrayList<Account> data) {
        this.accounts = data;
    }

    /**
     * Prepare numerous important RecyclerView elements.
     */
    @NonNull
    @NotNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Inflate cashflow_account_entry.xml here
        LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
        View itemView = inflater.inflate (R.layout.cashflow_account_entry, parent, false);
        AccountViewHolder accountViewHolder = new AccountViewHolder (itemView);

        // OnClickListener for Account tiles
        accountViewHolder.getContainer ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent i = new Intent(view.getContext (), CashflowAccountActivity.class);
                i.putExtra (Keys.KEY_CF_ACC, accounts.get (accountViewHolder.getBindingAdapterPosition ()));
                view.getContext ().startActivity (i);
                ((CashflowHomeActivity) view.getContext ()).finish ();
            }
        });

        return accountViewHolder;
    }

    /**
     * Set TextView texts when a ViewHolder is bound.
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull AccountViewHolder holder, int position) {
        Account currAccount = accounts.get (position);

        holder.setAccountName (currAccount.getName());
        holder.setAccountBalance (currAccount.getBalance());
        holder.setAccountType (currAccount.getType());
    }

    /**
     * Returns the size of the list of Accounts.
     *
     * @return the size of the list of Accounts
     */
    @Override
    public int getItemCount() {
        return this.accounts.size ();
    }
}
