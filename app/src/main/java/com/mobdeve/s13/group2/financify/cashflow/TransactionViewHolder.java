package com.mobdeve.s13.group2.financify.cashflow;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Transaction;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

/**
 * ViewHolder for the Transaction entries that will be shown via the RecyclerView.
 */
public class TransactionViewHolder extends RecyclerView.ViewHolder {
    // TextViews for each Transaction
    private TextView tvTransDesc, tvTransType, tvTransAmt, tvTransDate;

    // Container for each Transaction
    private ConstraintLayout clTile;

    /**
     * Constructor. Instantiates the attributes of this class.
     */
    public TransactionViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // Initialize attributes
        tvTransDesc = itemView.findViewById (R.id.tv_transaction_description);
        tvTransAmt = itemView.findViewById (R.id.tv_transaction_amount);
        tvTransType = itemView.findViewById (R.id.tv_transaction_type);
        tvTransDate = itemView.findViewById (R.id.tv_transaction_date);
        clTile = itemView.findViewById (R.id.cl_transaction_entry);
    }

    /**
     * Sets the TextView value of the description of this Transaction.
     *
     * @param desc  the description to be displayed
     */
    public void setTransDesc (String desc) {
        this.tvTransDesc.setText (desc);
    }

    /**
     * Sets the TextView value of the type of this Transaction. Also updates the color of the
     * Transaction's container.
     *
     * @param type  the type to be displayed
     */
    public void setTransType (String type) {
        this.tvTransType.setText (type);

        // Update color of container
        this.setTileColor (type);
    }

    /**
     * Sets the TextView value of the amount affiliated with this Transaction.
     *
     * @param amt   the amount to be displayed
     */
    public void setTransAmt (double amt) {
        this.tvTransAmt.setText (NumberFormat.getCurrencyInstance().format(amt));
    }

    /**
     * Sets the TextView value of the date of this Transaction.
     *
     * @param date  the date to be displayed
     */
    public void setTransDate (String date) {
        this.tvTransDate.setText (date);
    }

    /**
     * Sets the container color of this Transaction, given its type.
     *
     * @param type  the type of this Transaction
     */
    private void setTileColor (String type) {
        if (type.equalsIgnoreCase(Transaction.TYPE_EXPENSE)) {
            clTile.setBackgroundResource (R.drawable.tile_round_red);
        } else if (type.equalsIgnoreCase(Transaction.TYPE_INCOME)) {
            clTile.setBackgroundResource (R.drawable.tile_round_green);
        } else if (type.equalsIgnoreCase(Transaction.TYPE_INVESTMENT)) {
            clTile.setBackgroundResource (R.drawable.tile_round_yellow);
        }
    }

    /**
     * Returns the container of this Transaction.
     *
     * @return the ConstraintLayout container of this Transaction.
     */
    public ConstraintLayout getContainer () {
        return this.clTile;
    }
}
