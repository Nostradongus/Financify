package com.mobdeve.s13.group2.financify.reminders;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mobdeve.s13.group2.financify.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    // reference the data, as an arraylist
    private ArrayList<Reminders> reminder;

    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";

    // constructor for adapter
    public Adapter (ArrayList<Reminders> reminder) {
        this.reminder = reminder;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // inflate layout template.xml here
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.reminder_template, parent, false);

        ViewHolder myViewHolder = new ViewHolder(itemView);


        myViewHolder.getTvTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // launch the new activity
                // using intent (source(context), destination (class))
                Intent intent = new Intent(view.getContext(), SeeReminderActivity.class);
                intent.putExtra(KEY_TITLE, reminder.get(myViewHolder.getBindingAdapterPosition()).getTitle()); // string, data
                intent.putExtra(KEY_DESCRIPTION, reminder.get(myViewHolder.getBindingAdapterPosition()).getDescription());

                view.getContext().startActivity(intent);
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // everytime this gets called, youll go through an iteration
        // position is given
        Reminders currentReminder = this.reminder.get(position);
        holder.setTvDate(reminder.get(position).getDate());
        holder.setTvType(reminder.get(position).getType());
        holder.setTvDesc(reminder.get(position).getDescription());
        holder.setTvTitle(reminder.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return this.reminder.size();
    }
}

