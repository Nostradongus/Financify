package com.mobdeve.s13.group2.financify.reminders;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.R;

import java.util.ArrayList;

public class RemindersActivity extends BaseActivity {

    private RecyclerView rvReminderList;
    private RecyclerView.LayoutManager myManager;
    private Adapter myAdapter;
    private DataHelper helper;
    private ArrayList<Reminders> reminders;

    // add fab button
    private FloatingActionButton fabAdd;

    private ActivityResultLauncher myActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // GET INTENT
                        Intent i = result.getData();

                        // GET VALUES FROM THE INTENT BASED ON THE IDENTIFIERS IN ADD reminder ACTIVITY
                        String reminderTitle = i.getStringExtra(AddRemindersActivity.KEY_TITLE);
                        String reminderDesc = i.getStringExtra(AddRemindersActivity.KEY_DESCRIPTION);


                        // ADD TWEET TO DATA TO START OF RECYCLER VIEW
                        reminders.add(0, new Reminders(
                                reminderDesc,
                                "Urgent", reminderTitle,
                                "08-28-2021"
                        ));

//                        Adapter.notifyItemChanged(0);
//                        Adapter.notifyItemRangeChanged(0, Adapter.getItemCount());

                        //LOG.D (TAG, "onActivityResult: RESULT: success"
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        //LOG.D(TAG, "onActivityResult: RESULT: canceled"
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reminders);

        this.initFabAdd();
        this.initRecyclerView();
    }

    private void initFabAdd() {
        this.fabAdd = findViewById(R.id.fab_reminders_add);
        this.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (RemindersActivity.this, AddRemindersActivity.class);
                myActivityResultLauncher.launch(i);
            }
        });
    }

    private void initRecyclerView() {
        this.rvReminderList = findViewById(R.id.rv_reminders);

        this.myManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvReminderList.setLayoutManager(this.myManager);

        helper = new DataHelper();
        this.reminders = helper.initializeData();
        this.myAdapter = new Adapter(reminders);
        this.rvReminderList.setAdapter(this.myAdapter);

        // making new page and attaching it to the recycler view
        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(this.rvReminderList);
    }

    @Override
    public void onBackPressed() {
        // redirect back to home page
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // end current activity
    }
}