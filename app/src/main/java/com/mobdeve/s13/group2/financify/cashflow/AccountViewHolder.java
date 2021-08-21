package com.mobdeve.s13.group2.financify.cashflow;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group2.financify.R;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

/**
 * ViewHolder for the Account entries that will be shown via the RecyclerView.
 */
public class AccountViewHolder extends RecyclerView.ViewHolder {
    // TextViews for each Account
    private TextView tvAccountName, tvAccountType, tvAccountBalance;

    // Container for each Account
    private ConstraintLayout clTile;

    /**
     * Constructor. Instantiates the attributes of this class.
     */
    public AccountViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // Retrieve element IDs
        tvAccountName = itemView.findViewById (R.id.tv_account_name);
        tvAccountBalance = itemView.findViewById (R.id.tv_account_balance);
        tvAccountType = itemView.findViewById (R.id.tv_account_type);
        clTile = itemView.findViewById (R.id.cl_account_entry);
    }

    /**
     * Sets the TextView value of the name of this Account.
     *
     * @param name  the name of this account
     */
    public void setAccountName (String name) {
        this.tvAccountName.setText (name);
    }

    /**
     * Sets the TextView value of the type of this Account. Also updates the color of the
     * Account's container.
     *
     * @param type  the type of this account
     */
    public void setAccountType (String type) {
        this.tvAccountType.setText (type);

        // Update color of container
        this.setTileColor (type);
    }

    /**
     * Sets the TextView value of the current balance of this Account.
     *
     * @param bal   the current balance of this account
     */
    public void setAccountBalance (double bal) {
        this.tvAccountBalance.setText (NumberFormat.getCurrencyInstance().format(bal));
    }

    /**
     * Sets the container color of this Account, given its type.
     *
     * @param type  the type of this Account
     */
    private void setTileColor (String type) {
        if (type.equalsIgnoreCase(Account.TYPE_PHYSICAL)) {
            clTile.setBackgroundResource (R.drawable.tile_round_yellow);
        } else if (type.equalsIgnoreCase(Account.TYPE_BANK)) {
            clTile.setBackgroundResource (R.drawable.tile_round_green);
        } else if (type.equalsIgnoreCase(Account.TYPE_DIGITAL)) {
            clTile.setBackgroundResource (R.drawable.tile_round_blue);
        }
    }

    /**
     * Returns the container of this Account.
     *
     * @return the ConstraintLayout container of this Account.
     */
    public ConstraintLayout getContainer () {
        return this.clTile;
    }
}
