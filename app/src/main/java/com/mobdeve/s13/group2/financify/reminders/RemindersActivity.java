package com.mobdeve.s13.group2.financify.reminders;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.mobdeve.s13.group2.financify.DateHelper;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.cashflow.CashflowAccountActivity;
import com.mobdeve.s13.group2.financify.cashflow.CashflowHomeActivity;
import com.mobdeve.s13.group2.financify.cashflow.CashflowUpdateAccountActivity;
import com.mobdeve.s13.group2.financify.model.Reminder;
import com.mobdeve.s13.group2.financify.model.Transaction;

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
    private ConstraintLayout clFilterContainer;
    private SearchView svReminderSearch;
    private Spinner spRemType;
    private Button btnMonth, btnYear, btnClearFilter;
    private DatePickerDialog dpMonthDialog, dpYearDialog;
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

    // Home button
    private ConstraintLayout clHomeBtn;

    /**
     * This function is only run once.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Homepage layout of reminders
        setContentView(R.layout.activity_main_reminders);

        // Initialize General components
        this.initComponents ();
        // Initialize RecyclerView Components
        this.initRecyclerView();
        // Initialize date and time
        this.initTimeStamp();
        // Initialize FloatingActionButton
        this.initFabAdd();
        // Initialize Filter Components
        this.initFilters();
        // Initialize Firebase components
        this.initFirebase ();

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
     * Initialize general components.
     */
    private void initComponents () {
        clHomeBtn = findViewById (R.id.cl_rem_back_home_nav);

        // Back button for Account page
        clHomeBtn.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent i = new Intent (RemindersActivity.this, HomeActivity.class);
                startActivity (i);
                finish ();
            }
        });
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
                                    reminder.child("date").getValue().toString()
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
    private void initFilters() {
        // Initialize elements
        this.ibFilterBtn = findViewById (R.id.ib_reminders_filter);
        btnClearFilter = findViewById (R.id.btn_rem_clear_filter);
        clFilterContainer = findViewById (R.id.cl_reminder_filter);


        // Filter settings is invisible by default
        this.filterVisible = false;
        clFilterContainer.setVisibility (View.GONE);

        // Initialize other components
        initSpinner ();
        initDatePickers ();
        initSearch();

        // To toggle the visibility of the SearchView
        this.ibFilterBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (filterVisible) {
                    clFilterContainer.setVisibility(View.GONE);
                } else {
                    clFilterContainer.setVisibility(View.VISIBLE);
                }

                filterVisible = !filterVisible;
            }
        });

        // OnClickListener for Button that clears all filters
        btnClearFilter.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                clearFilters ();
            }
        });
    }

    private void initSearch() {
        // To make SearchView clickable from anywhere, not just on icon
        svReminderSearch = findViewById(R.id.sv_reminder_search);

        svReminderSearch.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                svReminderSearch.setIconified(false);
            }
        });

        // When queries are made through the SearchView
        svReminderSearch.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
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

    private void initDatePickers() {
        // Retrieve element IDs
        btnMonth = findViewById (R.id.btn_rem_month_filter);
        btnYear = findViewById (R.id.btn_rem_year_filter);

        // TODO: Figure out how to work with deprecated stuffs!
        System.out.println ("VERSION: " + android.os.Build.VERSION.SDK_INT);

        // For retrieving date today
        Calendar cal = Calendar.getInstance ();

        /* Month DatePickerDialog components */
        // Initialize Month DatePickerDialog (Filter for Month)
        dpMonthDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_DialogWhenLarge, new DatePickerDialog.OnDateSetListener() {
            // On selecting a month, trigger filter
            @Override
            public void onDateSet (DatePicker view, int year, int month, int dayOfMonth) {
                // Update button text
                btnMonth.setText (DateHelper.getMonthFormat (month + 1));
                // Trigger filter
                filterReminders ();
            }
        }, cal.get (Calendar.YEAR), cal.get (Calendar.MONTH), cal.get (Calendar.DAY_OF_MONTH)) {
            // For styling purposes only, removes additional background design
            @Override
            public void onCreate (Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };

        // To remove "Day" and "Year" inputs of DatePickerDialog for Month
        dpMonthDialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        dpMonthDialog.getDatePicker().findViewById(getResources().getIdentifier("year","id","android")).setVisibility(View.GONE);

        // OnClickListener for Button (to show DatePickerDialog for Month)
        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dpMonthDialog.show ();
            }
        });

        /* Year DatePickerDialog components */
        // Initialize Year DatePickerDialog (Filter for Year)
        dpYearDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_DialogWhenLarge, new DatePickerDialog.OnDateSetListener() {
            // On selecting a year, trigger filter
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update button text
                btnYear.setText (String.valueOf (year));
                // Trigger filter
                filterReminders ();
            }
        }, cal.get (Calendar.YEAR), cal.get (Calendar.MONTH), cal.get (Calendar.DAY_OF_MONTH)) {
            // For styling purposes only, removes additional background design
            @Override
            public void onCreate (Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
            }
        };

        // To remove "Day" and "Month" inputs of DatePickerDialog for Year
        dpYearDialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        dpYearDialog.getDatePicker().findViewById(getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);

        // OnClickListener for Button (to show DatePickerDialog for Year)
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dpYearDialog.show ();
            }
        });
    }

    private void initSpinner() {
        // Retrieve element ID
        spRemType = findViewById (R.id.sp_rem_type_filter);

        // Initialize "Type" Spinner
        ArrayAdapter<CharSequence> spTypeFilterAdapter = ArrayAdapter.createFromResource (
                this, R.array.reminder_types, android.R.layout.simple_spinner_item
        );
        spTypeFilterAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spRemType.setAdapter (spTypeFilterAdapter);

        // "Type" onItemSelectedListener (to trigger filtering of Transactions)
        spRemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterReminders ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterReminders() {
        // Retrieve filer from each setting
        String typeFilter = spRemType.getSelectedItem ().toString ().toLowerCase ();
        String monthFilter = btnMonth.getText ().toString ().toLowerCase ();
        String yearFilter = btnYear.getText ().toString ().toLowerCase ();

        /*
            GENERAL IDEA FOR FILTERING:
            (1) Filter main list based on TYPE first, store filtered content into a temporary list,
                replace main list contents with temporary list contents, and proceed to next filter.
            (2) Repeat for Month and Year filter.
            (3) At the end, the main list will contain elements that passed through all filters.
         */

        // Clear filtered list to avoid potential duplication
        reminders.clear ();
        // Restore original list
        reminders.addAll (reminderBackup);
        // Create a backup of the original list
        reminderBackup = new ArrayList<> (reminders);

        // Create a temporary list (for copying purposes)
        ArrayList<Reminder> temp = new ArrayList<> ();

        // If the user filtered based on TRANSACTION TYPE
        if (!typeFilter.equalsIgnoreCase ("Select type…")) {
            // Loop through each Transaction
            for (Reminder reminder : reminders) {
                // If "Type" filter matches Transaction type
                if (typeFilter.equalsIgnoreCase (reminder.getType ())) {
                    // Add to temporary list
                    temp.add(reminder);
                }
            }

            // Reset main list
            reminders.clear ();
            // Populate main list with filtered content
            reminders.addAll (temp);
            // Clear filtered list holder
            temp.clear ();


            // If the user resets the "Type" filter back to "none"
        } else {
            // Clear the main list, and restore from backup list
            reminders.clear ();
            reminders.addAll (reminderBackup);
        }

        // If the user filtered based on the MONTH of Transactions
        if (!monthFilter.equalsIgnoreCase ("none")) {
            // Loop through each Transaction
            for (Reminder reminder : reminders) {
                // If "Month" filter matches the Month of the Transaction
                if (monthFilter.equalsIgnoreCase (DateHelper.getMonthFormat (Integer.parseInt (reminder.getMonth ()))))
                    // Add to temporary list
                    temp.add (reminder);
            }

            // Reset main list
            reminders.clear ();
            // Populate main list with filtered content
            reminders.addAll (temp);
            // Clear filtered list holder
            temp.clear ();
        }

        // If the user filtered based on the YEAR of Transactions
        if (!yearFilter.equalsIgnoreCase ("none")) {
            // Loop through each Transaction
            for (Reminder reminder : reminders) {
                // If "Year" filter matches the Year of the Transaction
                if (yearFilter.equalsIgnoreCase (reminder.getYear ()))
                    // Add to temporary list
                    temp.add (reminder);
            }

            // Reset main list
            reminders.clear ();
            // Populate main list with filtered content
            reminders.addAll (temp);
            // Clear filtered list holder
            temp.clear ();
        }

        // Refresh RecyclerView
        myAdapter.notifyDataSetChanged ();

        // Show empty message, if applicable
        this.displayEmptyMessage ();
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
        btnMonth.setText (String.valueOf("NONE"));
        btnYear.setText (String.valueOf("NONE"));
        spRemType.setSelection (0);

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