package com.mobdeve.s13.group2.financify.reminders;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.cashflow.CashflowAccountActivity;
import com.mobdeve.s13.group2.financify.cashflow.CashflowHomeActivity;
import com.mobdeve.s13.group2.financify.cashflow.CashflowUpdateAccountActivity;
import com.mobdeve.s13.group2.financify.model.Reminder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This activity is the main page for the reminders.
 */
public class RemindersActivity extends BaseActivity {

    // RecyclerView Attributes
    private RecyclerView rvReminderList;
    private RecyclerView.LayoutManager myManager;
    private Adapter myAdapter;

    // List of reminders
    private ArrayList<Reminder> reminders;

    // Fab Add Button, to add reminders
    private FloatingActionButton fabAdd;
    private TextView tvEmptyMessage;

    // Filter Attributes
    private ImageButton ibFilterBtn;
    private SearchView svReminderSearch;
    private boolean filterVisible;
    private ArrayList<Reminder> reminderBackup;

    // add the date to display (current)
    private TextView currDate;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    // add the time to display (current)
    private TextView currTime;
    private SimpleDateFormat timeFormat;
    private String time;

    // Firebase Attributes
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference dbRef;
    private ProgressBar pbHome;

    /**
     * This function is only run once.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Homepage layout of reminders
        setContentView(R.layout.activity_main_reminders);

        // Initialize Firebase components
        this.initFirebase ();
        // Initialize RecyclerView Components
        this.initRecyclerView();
        // Initialize date and time
        this.initTimeStamp();
        // Initialize FloatingActionButton
        this.initFabAdd();
        // Initialize SearchView Components
        this.initSearchView();

    }

    /**
     * Show/hide empty message on activity resumption, if applicable
     */
    @Override
    protected void onResume () {
        super.onResume ();

        // Show empty message if there are no reminders on the database
        this.displayEmptyMessage ();
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        this.mAuth = FirebaseAuth.getInstance ();
        this.user = this.mAuth.getCurrentUser ();

        this.pbHome = findViewById (R.id.pb_reminders_home);
        this.pbHome.setVisibility (View.VISIBLE);

        if (this.user != null) {
            // valid session
            this.userId = this.user.getUid ();
            dbRef = FirebaseDatabase.getInstance ().getReference ("users").child (this.userId);

            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    // clear reminders
                    reminders.clear();

                    for (DataSnapshot reminder : snapshot.child("reminders").getChildren()) {
                        try {
                            // id, title, description, type, time
                            reminders.add(new Reminder(
                                    reminder.getKey(),
                                    reminder.child("title").getValue().toString(),
                                    reminder.child("description").getValue().toString(),
                                    reminder.child("type").getValue().toString(),
                                    reminder.child("time").getValue().toString()
                            ));
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }

                    reminderBackup = new ArrayList<> (reminders);
                    pbHome.setVisibility (View.GONE);
                    myAdapter.notifyDataSetChanged ();
                    displayEmptyMessage ();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    pbHome.setVisibility (View.GONE);
                }
            });
        } else {
            // relogin
            Toast.makeText (RemindersActivity.this, "Invalid session.", Toast.LENGTH_SHORT).show ();
        }
    }

    /**
     * This initializes the recycler view components
     */
    private void initRecyclerView() {
        // Instantiate reminders arraylist
        this.reminders = new ArrayList<>();

        // Initialize recyclerview for reminders
        this.rvReminderList = findViewById(R.id.rv_reminders);

        // Instantiate and attach lay out manager
        this.myManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        this.rvReminderList.setLayoutManager(this.myManager);

        // Instantiate and attach adapter
        myAdapter = new Adapter(reminders);
        this.rvReminderList.setAdapter(myAdapter);

        // Empty message for the recycler view
        this.tvEmptyMessage = findViewById(R.id.tv_reminders_empty);
        this.tvEmptyMessage.setVisibility(View.GONE);
    }

    private void initTimeStamp() {
        // for the current date being displayed
        this.currDate = findViewById(R.id.tv_reminders_current_date);

        // for the current time being displayed
        this.currTime = findViewById(R.id.tv_reminders_current_time);

        // setting up the date
        this.calendar = Calendar.getInstance();
        this.dateFormat = new SimpleDateFormat("EEE, d MMM ''yy");
        this.date = dateFormat.format(calendar.getTime());
        this.currDate.setText(date);

        // setting up the time
        this.timeFormat = new SimpleDateFormat("h:mm a");
        this.time = timeFormat.format(calendar.getTime());
        this.currTime.setText(time);
    }


    /**
     * This initializes all the search-view related components
     */
    private void initSearchView() {
        // Filter settings is invisible by default
        this.filterVisible = false;

        // Initialize elements
        this.ibFilterBtn = findViewById (R.id.ib_reminders_filter);
        this.svReminderSearch = findViewById (R.id.sv_reminder_search);
        this.svReminderSearch.setVisibility (View.GONE);

        // To toggle the visibility of the SearchView
        this.ibFilterBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (filterVisible) {
                    svReminderSearch.setVisibility(View.GONE);
                } else {
                    svReminderSearch.setVisibility(View.VISIBLE);
                }

                filterVisible = !filterVisible;
            }
        });

        // To make SearchView clickable from anywhere, not just on icon
        this.svReminderSearch.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                svReminderSearch.setIconified(false);
            }
        });

        // When queries are made through the SearchView
        this.svReminderSearch.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            // When query is submitted
            @Override
            public boolean onQueryTextSubmit(String query) {
                resetRecyclerViewContents ();

                // If there is query input
                if (query.trim ().length () > 0)
                    searchReminders (query);

                return false;
            }

            // When there is a change in text
            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length () == 0 &&
                        reminders != null &&
                        reminderBackup != null) {
                    resetRecyclerViewContents ();
                }

                return false;
            }
        });
    }

    /**
     * This initializes the FloatingAction Button component
     */
    private void initFabAdd() {
        // retrieve the id
        this.fabAdd = findViewById(R.id.fab_reminders_add);

        // listener of the button
        this.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                clearFilters ();
                Intent i = new Intent (RemindersActivity.this, AddRemindersActivity.class);

                i.putParcelableArrayListExtra (Keys.KEY_REMINDERS, reminders);

                startActivity (i);
                finish ();
            }
        });
    }

    /**
     * Reset the contents of the accounts ArrayList.
     */
    private void resetRecyclerViewContents () {
        // Clear filtered list
        reminders.clear ();
        // Restore original list
        reminders.addAll (reminderBackup);
        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        myAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }

    private void clearFilters() {
        svReminderSearch.setQuery(String.valueOf(""), false);

        // Clear filtered list
        reminders.clear ();
        // Restore original list
        reminders.addAll (reminderBackup);
        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        myAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        displayEmptyMessage ();
    }

    private void searchReminders(String query) {
        // Create a copy of reminders ArrayList
        reminderBackup = new ArrayList<> (reminders);

        // Empty reminders ArrayList
        reminders.clear ();

        // Add Accounts matching the query into the accounts ArrayList
        for (Reminder reminder : reminderBackup)
            if (reminder.getTitle ().toLowerCase ().contains (query.toLowerCase ()))
                reminders.add (reminder);

        // Notify Adapter that the list has changed (refreshes the RecyclerView)
        myAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
    }


    /**
     * This allows the app to display the empty message if the reminder list is empty
     */
    private void displayEmptyMessage() {
        if (this.reminders.isEmpty ())
            this.tvEmptyMessage.setVisibility (View.VISIBLE);
        else
            this.tvEmptyMessage.setVisibility (View.GONE);
    }

    @Override
    public void onBackPressed() {
        // redirect back to home page
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // end current activity
        finish ();
    }
}