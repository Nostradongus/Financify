package com.mobdeve.s13.group2.financify.reminders;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s13.group2.financify.R;

import org.jetbrains.annotations.NotNull;

public class ViewHolder extends RecyclerView.ViewHolder {

    public static Resources res;
    // adding the widgets
    private final TextView type;
    private final TextView description;
    private final TextView title;
    private final TextView date;


    public ViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // retrieving
        this.type = itemView.findViewById(R.id.tv_reminders_type);
        this.description = itemView.findViewById(R.id.tv_reminders_description);
        this.title = itemView.findViewById(R.id.tv_reminders_title);
        this.date = itemView.findViewById(R.id.tv_reminders_date);
        res = itemView.getContext().getResources();
    }

    public TextView getTvTitle () {
        return this.title;
    }

    public void setTvType (String type) {
        this.type.setText(type);
    }

    public void setTvDesc (String desc) {
        this.description.setText(desc);
    }

    public void setTvTitle (String title) {
        this.title.setText(title);
    }

    public void setTvDate (String date) {
        this.date.setText(date);
    }
}
