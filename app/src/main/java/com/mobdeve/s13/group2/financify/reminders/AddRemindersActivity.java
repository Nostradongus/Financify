package com.mobdeve.s13.group2.financify.reminders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.DateHelper;
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.layout.simple_spinner_item;

/**
 * Activity / page for adding new reminders to the user's reminder list.
 */
public class AddRemindersActivity extends BaseActivity {

    // UI Attributes
    private EditText et_title, et_desc;
    private DatePickerDialog datePickerDialog;
    private Spinner sp_reminder_type;
    private Button btn_date, btn_cancel, btn_add;

    // List of Reminders
    private ArrayList<Reminder> reminders;

    // Firebase Attributes
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add reminders layout
        setContentView(R.layout.activity_add_reminders);

        // to create a notification channel for the set reminder notifications above Oreo
        createNotificationChannel ();
        // Initialize the datepicker components
        initDatePicker();
        // Initialize components
        initComponents();
        // Initialize Spinner components
        initReminderTypeSpinner();
        // Initialize Firebase components
        initFirebase();
    }

    /**
     * Back button being pressed.
     */
    @Override
    public void onBackPressed () {
        // go back to home activity page
        goBackToRemindersPage ();
    }

    /**
     * This allows the back button to go back to the main home of the reminder feature
     */
    private void goBackToRemindersPage() {
        Intent i = new Intent (AddRemindersActivity.this, RemindersActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (AddRemindersActivity.this, LoginActivity.class);

        startActivity (i);
        finish ();
    }

    /**
     * Adds the newly created Reminder to the Firebase database
     *
     * @param title      the name of the new Reminder
     * @param description   the description of the new Reminder
     * @param type      the type of the new Reminder
     * @param date      the date of the new Reminder
     *
     * @return the id of the newly created reminder
     */
    private int addReminderInFirebase (String title, String description, String type, String date) {
        // New Reminder ID
        int newId;

        // If there are reminders, generate latest ID
        if (!reminders.isEmpty ())
            newId = Integer.parseInt (reminders.get (reminders.size () - 1).getId ()) + 1;
        else
            newId = 0;

        // New DB reference to the new Reminder
        DatabaseReference newRef = dbRef.child (String.valueOf(newId));

        // Set Reminder values in DB
        newRef.child ("title").setValue (title);
        newRef.child ("description").setValue (description);
        newRef.child ("type").setValue (type);
        newRef.child ("date").setValue (date);

        // return new reminder id to be used for notification setup
        return newId;
    }

    /**
     * Creates a notification channel for devices with API greater than Oreo.
     */
    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set notification components
            String name = "financify";
            String description = "financify Reminder!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // initialize and setup NotificationChannel
            NotificationChannel channel = new NotificationChannel("financify_notify", name, importance);
            channel.setDescription (description);
            channel.enableVibration (true);
            channel.enableLights (true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(notificationSound, audioAttributes);

            // create and establish NotificationChannel
            NotificationManager notificationManager = getSystemService (NotificationManager.class);
            notificationManager.createNotificationChannel (channel);
        }
    }

    /**
     * Sets notification for the newly created Reminder on the device.
     *
     * @param id    the id of the new Reminder
     * @param title the title of the new Reminder
     * @param date  the date of the new Reminder
     */
    private void setNotification (int id, String title, String date) {
        // initialize and setup notification intent with new reminder's data
        Intent intent = new Intent (AddRemindersActivity.this, ReminderBroadcast.class);
        intent.putExtra("NOTIFICATION_ID", id);
        intent.putExtra("NOTIFICATION_TITLE", title);

        PendingIntent pIntent = PendingIntent.getBroadcast (AddRemindersActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService (ALARM_SERVICE);

        // separate month, day, and year values and parse them
        String[] dateVals = date.split("/");
        int month = Integer.parseInt(dateVals[0]) - 1;
        int day = Integer.parseInt(dateVals[1]);
        int year = Integer.parseInt(dateVals[2]);

        // set notification date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // set to receive at exactly 8:00 AM of the new Reminder's date
        calendar.set(year, month, day, 8, 0, 0);

        // start notification
        alarmManager.set (AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ();

        // If valid session
        if (user != null) {
            // Retrieve user ID
            userId = user.getUid ();
            // Create DB reference to this user's Cashflow accounts
            dbRef = FirebaseDatabase.getInstance ().getReference ()
                    .child ("users")
                    .child (userId)
                    .child ("reminders");
        }
        // If invalid session
        else {
            goBackToLogin ();
        }
    }

    /**
     * Initialize all the general components.
     */
    private void initComponents () {
        // Retrieve element IDs
        et_title = findViewById (R.id.et_rem_reminder_title);
        et_desc = findViewById (R.id.et_rem_reminder_desc);
        btn_cancel = findViewById (R.id.btn_rem_cancel_add_reminder);
        btn_date = findViewById (R.id.btn_rem_reminder_date);
        btn_add = findViewById (R.id.btn_rem_add_reminders);

        // Retrieve reminders passed from Intent (using Parcelable)
        Intent i = getIntent ();
        // Instantiate reminders list
        reminders = new ArrayList<> (i.getParcelableArrayListExtra (Keys.KEY_REMINDERS));

        // Cancel Button OnClickListener
        btn_cancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToRemindersPage ();
            }
        });

        // Set default date
        btn_date.setText (DateHelper.getDateToday ());

        // Date Button OnClickListener
        btn_date.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                openDatePicker ();
            }
        });

        // Add Button OnClickListener
        // Add Button OnClickListener
        btn_add.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // If all fields are valid
                if (isValidForm ()) {
                    // Retrieve form data
                    String title = et_title.getText ().toString ();
                    String desc = et_desc.getText ().toString ();
                    String type = sp_reminder_type.getSelectedItem ().toString ();
                    String date = btn_date.getText().toString();

                    // Add to Firebase
                    int id = addReminderInFirebase (title, desc, type, date);

                    // set reminder notification on device
                    setNotification(id, title, date);

                    // End activity
                    goBackToRemindersPage ();
                }
            }
        });
    }


    /**
     * Initialize Spinner components.
     */
    private void initReminderTypeSpinner () {
        // Retrieve element ID
        this.sp_reminder_type = findViewById (R.id.sp_rem_reminder_type);

        // Retrieve & Attach Spinner contents
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource (this, R.array.reminder_types, simple_spinner_item);
        spinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        sp_reminder_type.setAdapter (spinnerAdapter);
    }

    /**
     * Initialize DatePicker components.
     */
    private void initDatePicker () {
        // OnDateSetListener for DatePickerDialog
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet (DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                // String date = DateHelper.makeDateString (day, month, year);
                String date = month + "/" + day + "/" + year;
                btn_date.setText (date);
            }
        };

        // Get date today
        Calendar cal = Calendar.getInstance ();
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int day = cal.get (Calendar.DAY_OF_MONTH);

        // Set default date for DatePickerDialog
        datePickerDialog = new DatePickerDialog (this, R.style.DatePicker, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker ().setMinDate (cal.getTimeInMillis ());
    }

    /**
     * Shows the DatePickerDialog for this activity.
     */
    public void openDatePicker() {
        datePickerDialog.show();
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        boolean isValid = true;

        // REMINDER TITLE
        if (et_title.getText ().toString ().trim ().isEmpty ()) {
            et_title.setError ("Please input a reminder title!");
            isValid = false;
        }

        if (isExistingReminderTitle (et_title.getText ().toString ().trim ())) {
            et_title.setError ("Reminder title already taken!");
            isValid = false;
        }

        // REMINDER DESCRIPTION
        if (et_desc.getText ().toString ().trim ().isEmpty ()) {
            et_desc.setError ("Please input a short description!");
            isValid = false;
        }

        // REMINDER TYPE
        if (sp_reminder_type.getSelectedItem ().toString ().equalsIgnoreCase ("Select type…")) {
            TextView errorText = (TextView) sp_reminder_type.getSelectedView ();
            errorText.setTextColor (Color.RED);
            errorText.setText (new String ("Select type…"));
            isValid = false;
        }

        // REMINDER DATE
        if (!isValid) {
            Toast.makeText (this, "Please fill up all fields!", Toast.LENGTH_SHORT).show ();
        } else if (DateHelper.isDateInPast (btn_date.getText ().toString ().trim ())) {
            Toast.makeText (this, "Date cannot be in the past!", Toast.LENGTH_SHORT).show ();
            isValid = false;
        }


        return isValid;
    }

    /**
     * Checks if a given Reminder Title already exists.
     *
     * @param title  the reminder title to be checked
     * @return  true if existing title, otherwise false
     */
    private boolean isExistingReminderTitle(String title) {
        for (Reminder reminder : reminders)
            if (reminder.getTitle ().equalsIgnoreCase (title))
                return true;

        return false;
    }
}
