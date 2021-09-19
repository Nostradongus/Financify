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
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Reminder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * See reminder activity / page, views a specific reminder entry pressed by the user.
 */
public class SeeReminderActivity extends BaseActivity {

    // UI Attributes
    private EditText etTitle, etDesc;
    private Button btnDate, btnUpdate, btnCancel, btnDelete;
    private DatePickerDialog datePickerDialog;
    private Spinner spType;

    // reminder to be edited
    private Reminder reminder;

    // firebase attributes
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Edit reminder layout
        setContentView(R.layout.activity_see_reminder);

        // to create a notification channel for the updated reminder notifications above Oreo
        createNotificationChannel ();
        // Initialize General Components
        initComponents();
        // Initialize DatePicket for the date
        initDatePicker();
        // Initialize Spinner components of rthe type
        initSpinners();
        // Initialize the firebase components
        initFirebase();
    }

    /**
     * Pressing the back button must lead the user to the reminders home
     */
    @Override
    public void onBackPressed () {
        // go back to the main
        goBackToRemindersPage();
    }

    /**
     * Launches an activity leading to the Reminder's Main page and finishes this activity.
     */
    private void goBackToRemindersPage () {
        Intent i = new Intent (SeeReminderActivity.this, RemindersActivity.class);
        i.putExtra (Keys.KEY_REMINDERS, reminder);
        startActivity (i);
        finish ();
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (SeeReminderActivity.this, LoginActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Creates a notification channel for devices with API greater than Oreo.
     */
    private void createNotificationChannel() {
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
     * Updates notification for the existing updated Reminder on the device.
     *
     * @param id    the id of the updated Reminder
     * @param title the title of the updated Reminder
     * @param date  the date of the updated Reminder
     */
    private void updateNotification (int id, String title, String date) {
        // initialize and setup notification intent with new reminder's data
        Intent intent = new Intent (SeeReminderActivity.this, ReminderBroadcast.class);
        intent.putExtra("NOTIFICATION_ID", id);
        intent.putExtra("NOTIFICATION_TITLE", title);

        PendingIntent pIntent = PendingIntent.getBroadcast (
                SeeReminderActivity.this, 0, intent, 0
        );
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
     * Updates the Reminder in the Firebase database.
     *
     * @param   title   the updated name of the Reminder
     * @param   desc    the updated description of the Reminder
     * @param   type    the updated type of the Reminder
     * @param   date    the updated date of the Reminder
     */
    private void updateReminderInFirebase (String title, String desc, String type, String date) {
        // Create new reference
        DatabaseReference newRef = dbRef.child (reminder.getId ());

        // Set DB values
        newRef.child ("title").setValue (title);
        newRef.child ("description").setValue (desc);
        newRef.child ("type").setValue (type);
        newRef.child ("date").setValue (date);
    }

    /**
     * Deletes the current reminder in the Firebase database.
     */
    private void deleteReminderInFirebase () {
        // Remove child
        dbRef.child (reminder.getId ()).removeValue ();
    }

    /**
     * Initialize Firebase components.
     */
    private void initFirebase () {
        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ();

        // If valid session
        if (user != null) {
            userId = user.getUid ();
            dbRef = FirebaseDatabase.getInstance ().getReference ()
                    .child ("users")
                    .child (userId)
                    .child ("reminders");
            // If invalid session
        } else {
            goBackToLogin ();
        }
    }

    /**
     * Initialize general components.
     */
    private void initComponents () {
        // Retrieve element IDs
        this.etTitle = findViewById (R.id.et_rem_update_title);
        this.etDesc = findViewById (R.id.et_rem_update_desc);
        this.btnDate = findViewById(R.id.btn_rem_update_date);

        this.btnUpdate = findViewById (R.id.btn_rem_update_entry);
        this.btnDelete = findViewById (R.id.btn_rem_delete_entry);
        this.btnCancel = findViewById (R.id.btn_rem_cancel_update_entry);

        // Cancel Button OnClickListener
        btnCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                goBackToRemindersPage ();
            }
        });

        // Update Button OnClickListener
        btnUpdate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // If valid form
                if (isValidForm ()) {
                    // Retrieve fields
                    String title = etTitle.getText ().toString ();
                    String desc = etDesc.getText ().toString ();
                    String type = spType.getSelectedItem ().toString ();
                    String date = btnDate.getText ().toString ();

                    // Update in Firebase
                    updateReminderInFirebase (title, desc, type, date);

                    // update reminder notification if date was changed
                    if (!reminder.getDate().equalsIgnoreCase(date)) {
                        int id = Integer.parseInt(reminder.getId());
                        updateNotification(id, title, date);
                    }

                    // Update local Account
                    reminder.setTitle (title);
                    reminder.setDesc(desc);
                    reminder.setType (type);
                    reminder.setDate(date);

                    // Exit activity
                    goBackToRemindersPage ();
                    // If invalid form
                } else {
                    Toast.makeText (SeeReminderActivity.this, "Please fill up all fields!", Toast.LENGTH_SHORT).show ();
                }
            }
        });

        // Delete Button OnClickListener
        btnDelete.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                // Delete Account in Firebase
                deleteReminderInFirebase ();

                // Exit activity
                goBackToRemindersPage ();
            }
        });

        // Retrieve Reminder from Reminder page
        Intent i = getIntent ();
        reminder = i.getParcelableExtra (Keys.KEY_REMINDERS);

//        // Set field value
        etTitle.setText (reminder.getTitle ());
        etDesc.setText (reminder.getDesc ());
        btnDate.setText (reminder.getDate ());

        // Date Button OnClickListener
        btnDate.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                openDatePicker ();
            };
        });
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
                String date = month + "/" + day + "/" + year;
                btnDate.setText (date);
            }
        };

        // Get date today
        Calendar cal = Calendar.getInstance ();
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int day = cal.get (Calendar.DAY_OF_MONTH);

        // Set default date for DatePickerDialog
        datePickerDialog = new DatePickerDialog (this, R.style.DatePicker, dateSetListener, year, month, day);
        datePickerDialog = new DatePickerDialog (this, R.style.DatePicker, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker ().setMinDate (cal.getTimeInMillis ());
    }

    /**
     * Initialize Spinner components.
     */
    private void initSpinners () {
        // Retrieve "Type" Spinner ID
        spType = findViewById (R.id.sp_rem_update_type);

        // Set "Type" Spinner content using resource file
        ArrayAdapter<CharSequence> spTypeAdapter = ArrayAdapter.createFromResource (
                this, R.array.reminder_types, android.R.layout.simple_spinner_item
        );

        // Set "Type" Spinner View Resource & Adapter
        spTypeAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter (spTypeAdapter);

        // Set Spinner selection to Transaction type
        List<String> remindersTypes = Arrays.asList(getResources().getStringArray(R.array.reminder_types));
        spType.setSelection (remindersTypes.indexOf (reminder.getType ()));
    }

    private void openDatePicker() {
        datePickerDialog.show ();
    }

    /**
     * Checks if all fields are properly filled.
     *
     * @return  true if all fields are valid, otherwise false
     */
    private boolean isValidForm () {
        boolean isValid = true;

        // REMINDER TITLE
        if (etTitle.getText ().toString ().trim ().isEmpty ()) {
            etTitle.setError ("Please input a reminder title!");
            isValid = false;
        }

        // REMINDER DESCRIPTION

        if (etDesc.getText ().toString ().trim ().isEmpty ()) {
            etDesc.setError ("Please input a short description!");
            isValid = false;
        }

        // REMINDER TYPE
        if (spType.getSelectedItem ().toString ().equalsIgnoreCase ("Select type…")) {
            TextView errorText = (TextView) spType.getSelectedView ();
            errorText.setTextColor (Color.RED);
            errorText.setText (new String ("Select type…"));
            isValid = false;
        }

        return isValid;
    }
}