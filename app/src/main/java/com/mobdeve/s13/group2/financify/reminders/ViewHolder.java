package com.mobdeve.s13.group2.financify.reminders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Reminder;

import org.jetbrains.annotations.NotNull;

/**
 * ViewHolder class for Reminder List RecyclerView
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    // textview for the template of reminder
    private final TextView title;
    private final TextView description;
    private final TextView type;
    private final TextView date;

    // container for eahc reminder
    private ConstraintLayout clReminder;


    /**
     * Constructor of the View Holder to instantiate the class
     *
     * @param itemView
     */
    public ViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // retrieving
        this.title = itemView.findViewById(R.id.tv_reminders_title);
        this.description = itemView.findViewById(R.id.tv_reminders_description);
        this.type = itemView.findViewById(R.id.tv_reminders_type);
        this.date = itemView.findViewById(R.id.tv_reminders_date);
        this.clReminder = itemView.findViewById(R.id.cl_reminder_entry);
    }

    /**
     * This sets the Textview value of the title of the reminder.
     */
    public void setReminderTitle(String title) {
        this.title.setText(title);
    }

    /**
     * This sets the textview value of the description of the reminder
     */
    public void setDescription(String desc) {
        this.description.setText(desc);
    }

    /**
     * This sets the reminder type
     */
    public void setType(String type) {
        this.type.setText(type);

        // change the color of the title
        // NOTE: If urgent, yellow. If not, green
        this.setColor(type);
    }

    /**
     * This sets the time of the reminder, when it will be due
     *
     * @param date
     */
    public void setDate(String date) {
        this.date.setText(date);
    }

    /**
     * This sets the color of the reminder, given its type
     *
     * @param type
     */
    private void setColor(String type) {
        if (type.equalsIgnoreCase(Reminder.TYPE_URGENT)) {
            clReminder.setBackgroundResource(R.drawable.tile_round_blue);
        } else if (type.equalsIgnoreCase(Reminder.TYPE_NON)) {
            clReminder.setBackgroundResource(R.drawable.tile_round_yellow);
        }
    }

    /**
     * Returns the container of this Reminder.
     *
     * @return the ConstraintLayout container of this Account.
     */
    public ConstraintLayout getContainer() {
        return this.clReminder;
    }
}