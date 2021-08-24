package com.mobdeve.s13.group2.financify.summary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.DateHelper;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.model.Account;

import java.util.ArrayList;
import java.util.Calendar;

// TODO: add documentation
public class SummaryActivity extends BaseActivity {

    // UI components
    private ConstraintLayout clHomeBtn;
    private ProgressBar pbSummary;

    // summary statistics components
    private PieData pieDataAccounts, pieDataRatios;
    private String titleAccounts, titleRatios;
    private ArrayList<String> textAccounts, textRatios;
    private PieChart pieChart;
    private TextView tvSummaryTitle;
    private TextView[] tvSummaryTexts;

    // Preferences / filter box components
    private ImageButton ibPrefs;
    private ConstraintLayout clPrefsBox;
    private boolean prefsVisible;
    private Spinner spChartType, spSummaryType;
    private Button btnMonth, btnYear, btnClearPrefs;
    private DatePickerDialog dpMonthDialog, dpYearDialog;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    // user's list of accounts
    private ArrayList<Account> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // initialize view components in this activity
        initComponents();

        // initialize preference / filter box
        initPreferencesBox();

        // TODO: to be updated when backend is implemented
//        // initialize data with Firebase
//        initFirebase();

        // initialize data for statistics
        initData();

        // initialize summary texts
        initSummary();

        // initialize pie chart with data
        initChart();
    }

    private void initComponents() {
        // initialize preference box components
        ibPrefs = findViewById(R.id.ib_summary_filter_box);
        clPrefsBox = findViewById(R.id.cl_summary_filter_box);
        btnClearPrefs = findViewById(R.id.btn_summary_clear_filter);

        // initialize spinner component from preferences / filter box
        spSummaryType = findViewById(R.id.sp_summary_type_filter);

        // initialize chart components for graphical view
        pieChart = findViewById(R.id.pie_chart);

        // initialize progress bar for chart loading and updating
        pbSummary = findViewById(R.id.pb_summary);

        // initialize summary text components for numerical text view
        textAccounts = new ArrayList<>();
        textRatios = new ArrayList<>();
        tvSummaryTitle = findViewById(R.id.tv_summary_numerical_title);
        tvSummaryTexts = new TextView[4];
        tvSummaryTexts[0] = findViewById(R.id.tv_summary_placeholder_1);
        tvSummaryTexts[1] = findViewById(R.id.tv_summary_placeholder_2);
        tvSummaryTexts[2] = findViewById(R.id.tv_summary_placeholder_3);
        tvSummaryTexts[3] = findViewById(R.id.tv_summary_placeholder_4);

        // initialize home button layout
        clHomeBtn = findViewById(R.id.cl_summary_back_home_nav);

        // set listener for home button layout
        clHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToHomePage();
            }
        });
    }

    private void initPreferencesBox() {
        // default values
        prefsVisible = false;
        clPrefsBox.setVisibility(View.GONE);

        // initialize other components in preference / filter box
        initSpinners();
        initDatePickers();

        // toggle visibility of preference / filter box
        ibPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefsVisible) {
                    clPrefsBox.setVisibility(View.GONE);
                } else {
                    clPrefsBox.setVisibility(View.VISIBLE);
                }

                prefsVisible = !prefsVisible;
            }
        });

        // for resetting preferences made by user
        btnClearPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear and reset displayed data
                clearPreferences();
            }
        });
    }

    // TODO: complete code here, get all accounts of logged in user
    private void initFirebase() {

    }

    // TODO: HARDCODED FOR NOW; to be revised and updated when backend is implemented
    private void initData() {
        // initialize pie chart data entries for "top accounts by balance"
        ArrayList<PieEntry> pieAccounts = new ArrayList<>();
        pieAccounts.add(new PieEntry(50, "MyBanko"));
        pieAccounts.add(new PieEntry(25, "BDO"));
        pieAccounts.add(new PieEntry(15, "Lincoln"));
        pieAccounts.add(new PieEntry(10, "Others"));

        PieDataSet pieDataSetAccounts = new PieDataSet(pieAccounts, "Top Accounts By Balance");
        pieDataSetAccounts.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSetAccounts.setValueTextColor(Color.BLACK);
        pieDataSetAccounts.setValueTextSize(14f);
        pieDataSetAccounts.setValueFormatter(new PercentFormatter());

        // set pie accounts data set
        pieDataAccounts = new PieData(pieDataSetAccounts);

        // initialize pie chart data for "income, investment, and expense ratios"
        ArrayList<PieEntry> pieRatios = new ArrayList<>();
        pieRatios.add(new PieEntry(70, "Income"));
        pieRatios.add(new PieEntry(10, "Investment"));
        pieRatios.add(new PieEntry(20, "Expense"));

        PieDataSet pieDataSetRatios = new PieDataSet(pieRatios, "Income, Investment, and Expense Ratio");
        pieDataSetRatios.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSetRatios.setValueTextColor(Color.BLACK);
        pieDataSetRatios.setValueTextSize(14f);
        pieDataSetRatios.setValueFormatter(new PercentFormatter());

        // set pie ratios data set
        pieDataRatios = new PieData(pieDataSetRatios);

        // initialize summary numerical texts
        titleAccounts = "Top accounts by balance in 2021";
        titleRatios = "Income, Investment, and Expense Ratio in 2021";

        textAccounts.clear();
        textAccounts.add("MyBanko - 50%");
        textAccounts.add("BDO - 25%");
        textAccounts.add("Lincoln - 15%");
        textAccounts.add("Others - 10%");

        textRatios.clear();
        textRatios.add("Income - 70%");
        textRatios.add("Investment - 10%");
        textRatios.add("Expense - 20%");
    }

    // TODO: to be updated when backend is implemented
    private void initChart() {
        // start progress bar for loading indicator
        pbSummary.setVisibility(View.VISIBLE);

        // initialize pie chart with default data (top accounts by balance)
        pieChart.setData(pieDataAccounts);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Top Accounts By Balance");

        // animate pie chart
        pieChart.animate();
        pieChart.spin( 500,0,360f, Easing.EaseInOutQuad);

        // stop progress bar as chart loading process is complete
        pbSummary.setVisibility(View.GONE);
    }

    // TODO: HARDCODED FOR NOW; to be updated when backend is implemented
    private void initSummary() {
        tvSummaryTitle.setText(titleAccounts);
        tvSummaryTexts[0].setText(textAccounts.get(0));
        tvSummaryTexts[1].setText(textAccounts.get(1));
        tvSummaryTexts[2].setText(textAccounts.get(2));
        tvSummaryTexts[3].setText(textAccounts.get(3));
    }

    private void initSpinners() {
        // initialize values for summary type spinner
        ArrayAdapter<CharSequence> spSummaryTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.summary_types, android.R.layout.simple_spinner_item
        );
        spSummaryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSummaryType.setAdapter(spSummaryTypeAdapter);

        // listener for checking which type of summary to display
        spSummaryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSelectSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initDatePickers() {
        // initialize date picker components inside preferences / filter box
        btnMonth = findViewById (R.id.btn_summary_month_filter);
        btnYear = findViewById (R.id.btn_summary_year_filter);

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
                updateData ();
            }
        }, cal.get (Calendar.YEAR), cal.get (Calendar.MONTH), cal.get (Calendar.DAY_OF_MONTH)) {
            // For styling purposes only, removes additional background design
            @Override
            public void onCreate (Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable (Color.TRANSPARENT));
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
                updateData();
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

    // TODO: to be updated when backend is implemented
    private void clearPreferences() {
        // reset month and year buttons
        btnMonth.setText("NONE");
        btnYear.setText("NONE");

        // reset summary type selected value
        spSummaryType.setSelection(0);

        // reset data to be displayed graphically and textually
        initSummary();
        initChart();
    }

    private void onSelectSummary() {
        String type = spSummaryType.getSelectedItem().toString();

        if (type.equalsIgnoreCase("Top Accounts By Balance")) {
            tvSummaryTitle.setText(titleAccounts);
            tvSummaryTexts[0].setText(textAccounts.get(0));
            tvSummaryTexts[1].setText(textAccounts.get(1));
            tvSummaryTexts[2].setText(textAccounts.get(2));
            tvSummaryTexts[3].setVisibility(View.VISIBLE);
            tvSummaryTexts[3].setText(textAccounts.get(3));

            // update pie chart with chosen data (income, investment, and expense ratio)
            pieChart.setData(pieDataAccounts);
            pieChart.setCenterText("Top Accounts By Balance");
        } else {
            tvSummaryTitle.setText(titleRatios);
            tvSummaryTexts[0].setText(textRatios.get(0));
            tvSummaryTexts[1].setText(textRatios.get(1));
            tvSummaryTexts[2].setText(textRatios.get(2));
            tvSummaryTexts[3].setText("");
            tvSummaryTexts[3].setVisibility(View.GONE);

            // update pie chart with chosen data (income, investment, and expense ratio)
            pieChart.setData(pieDataRatios);
            pieChart.setCenterText("Income, Investment, and Expense Ratio");
        }

        // set no description
        pieChart.getDescription().setEnabled(false);

        // refresh pie chart
        pieChart.invalidate();

        // spin pie chart
        pieChart.spin( 500,0,360f, Easing.EaseInOutQuad);
    }

    // TODO: to complete when backend is implemented
    private void updateData() {
        // TODO: add data querying codes here with user's list of accounts

        // update summary texts
        updateSummary();

        // update chart
        updateChart();
    }

    // TODO: to complete when backend is implemented
    private void updateSummary() {
        // code here to update summary texts (below the chart)
    }

    // TODO: to complete when backend is implemented
    private void updateChart() {
        // code here to update chart data (both bar and pie)
    }

    private void goBackToHomePage() {
        // return back to home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // finish current activity
        finish();
    }

    @Override
    public void onBackPressed() {
        // go back to home activity
        goBackToHomePage();
    }
}