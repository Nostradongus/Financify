package com.mobdeve.s13.group2.financify.reminders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Reminder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    // reference the data, as an arraylist
    private ArrayList<Reminder> reminderList;

    /**
     * Construtor of this adapter for reminders
     * @param data
     */
    public Adapter (ArrayList<Reminder> data) {
        this.reminderList = data;
    }

    /**
     * Prepare numerous important RecyclerView elements.
     */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // inflate layout template.xml here
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.reminder_template, parent, false);

        ViewHolder reminderViewHolder = new ViewHolder(itemView);

        // onclick listener for the reminders being displayed
        reminderViewHolder.getContainer ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent i = new Intent(view.getContext (), SeeReminderActivity.class);
                i.putExtra (Keys.KEY_REMINDERS, reminderList.get (reminderViewHolder.getBindingAdapterPosition ()));
                view.getContext ().startActivity (i);
                ((RemindersActivity) view.getContext ()).finish ();
            }
        });
        return reminderViewHolder;
    }

    /**
     * This sets the textview texts
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // everytime this gets called, youll go through an iteration
        // position is given
        Reminder currentReminder = reminderList.get(position);

        holder.setReminderTitle(currentReminder.getTitle());
        holder.setDescription(currentReminder.getDesc());
        holder.setType(currentReminder.getType());
        holder.setDate(currentReminder.getDate());
    }

    /**
     * Returns the size of the list of reminders
     * @return
     */
    @Override
    public int getItemCount() {
        return this.reminderList.size();
    }
}

