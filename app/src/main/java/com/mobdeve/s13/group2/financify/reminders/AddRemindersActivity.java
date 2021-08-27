package com.mobdeve.s13.group2.financify.reminders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobdeve.s13.group2.financify.R;

import org.w3c.dom.Text;

public class AddRemindersActivity extends Activity {

    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";

    // REFER TO THE WIDGETS ON THE LAYOUT
    private Button btn_cancel;
    private Button btn_add;
    private EditText et_desc;
    private EditText et_title;
    private EditText et_type;
    private EditText et_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminders);

        this.btn_cancel = findViewById(R.id.btn_rem_cancel_add_reminder);
        this.btn_add = findViewById(R.id.btn_rem_add_reminders);
        this.et_title = findViewById(R.id.et_rem_reminder_title);
        this.et_desc = findViewById(R.id.et_rem_reminder_desc);

        // ACCEPT THE VALUES AND RETURN TO MAIN ACTIVITY
        this.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String desc = et_desc.getText().toString();

                if (title.length() > 0 && desc.length() > 0) {
                    Intent i = new Intent();
                    i.putExtra(KEY_TITLE, title);
                    i.putExtra(KEY_DESCRIPTION, desc);
                    setResult(Activity.RESULT_OK, i);
                    finish(); // to prevent multiple layer actyivities

                }
                else {
                    // post toast
                }
            }
        });

        // ACCEPT THE VALUES AND RETURN TO MAIN ACTIVITY
        this.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (AddRemindersActivity.this, MainActivity.class);
                startActivity (i);
                finish ();
            }

            });


    }
}
