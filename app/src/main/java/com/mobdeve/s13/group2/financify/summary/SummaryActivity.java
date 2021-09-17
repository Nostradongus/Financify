package com.mobdeve.s13.group2.financify.summary;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s13.group2.financify.BaseActivity;
import com.mobdeve.s13.group2.financify.DateHelper;
import com.mobdeve.s13.group2.financify.HomeActivity;
import com.mobdeve.s13.group2.financify.LoginActivity;
import com.mobdeve.s13.group2.financify.R;
import com.mobdeve.s13.group2.financify.cashflow.CashflowAccountActivity;
import com.mobdeve.s13.group2.financify.model.Account;
import com.mobdeve.s13.group2.financify.model.Model;
import com.mobdeve.s13.group2.financify.model.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Comparator for sorting accounts according to their balance in descending order.
 */
class BalanceComparator implements Comparator<Account> {
    @Override
    public int compare(Account o1, Account o2) {
        if (o1.getBalance() > o2.getBalance()) {
            return -1;
        } else if (o1.getBalance() < o2.getBalance()) {
            return 1;
        }
        return 0;
    }
}

/**
 * For summary activity / page, shows the summary statistics of the user's data.
 */
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

    // empty indicator messages
    private TextView tvEmpty;
    private TextView tvDateEmpty;

    // Preferences / filter box components
    private ImageButton ibPrefs;
    private ConstraintLayout clPrefsBox;
    private boolean prefsVisible;
    private Spinner spSummaryType;
    private Button btnMonth, btnYear, btnClearPrefs;
    private TextView tvMonthLabel, tvYearLabel;
    private DatePickerDialog dpMonthDialog, dpYearDialog;

    // Firebase components
    private DatabaseReference dbRef;

    // user's list of accounts (for Top Accounts By Balance)
    private ArrayList<Account> accounts;

    // backup for user's list of accounts
    private ArrayList<Account> accountsBackup;

    // user's list of transactions from all accounts (for Income, Investment, and Expense Ratio)
    private ArrayList<Transaction> transactions;

    // backup for user's list of transactions from all accounts
    private ArrayList<Transaction> transactionsBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // initialize view components in this activity
        initComponents();

        // initialize preference / filter box
        initPreferencesBox();

        // initialize data with Firebase and get needed data for summary statistics
        initFirebase();
    }

    /**
     * Initializes UI components.
     */
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

        // initialize list of accounts
        accounts = new ArrayList<>();
        accountsBackup = new ArrayList<>();

        // initialize list of transactions per account
        transactions = new ArrayList<>();
        transactionsBackup = new ArrayList<>();

        // initialize empty indicator messages
        tvEmpty = findViewById(R.id.tv_summary_empty);
        tvDateEmpty = findViewById(R.id.tv_summary_date_empty);

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

    /**
     * Initializes Preferences (Filter) Options Box.
     */
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

    /**
     * Initializes Firebase components.
     */
    private void initFirebase() {
        // get current user logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // if there is a user logged in (or in session)
        if (user != null) {
            // get reference to user's list of accounts from the database
            dbRef = FirebaseDatabase.getInstance().getReference()
                    .child(Model.users.name())
                    .child(user.getUid())
                    .child(Model.accounts.name());

            // get user's list of accounts
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    // try and catch NullPointerException
                    try {
                        // loop through user's list of accounts from the database
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            // temporary ArrayList to store transactions from current account
                            ArrayList<Transaction> temp = new ArrayList<>();

                            // Loop through received Transactions
                            for (DataSnapshot transaction : postSnapshot.child ("transactions").getChildren ()) {
                                // Instantiate a Transaction object per child
                                temp.add (new Transaction (
                                        transaction.getKey (),
                                        transaction.child ("description").getValue ().toString (),
                                        Double.parseDouble (transaction.child ("amount").getValue ().toString ()),
                                        transaction.child ("type").getValue ().toString (),
                                        transaction.child ("date").getValue ().toString (),
                                        transaction.child ("accountId").getValue ().toString ()
                                ));
                            }

                            // get current account
                            Account account = new Account (
                                    postSnapshot.getKey (),
                                    postSnapshot.child ("name").getValue ().toString (),
                                    Double.parseDouble (postSnapshot.child ("balance").getValue ().toString ()),
                                    postSnapshot.child ("type").getValue ().toString (),
                                    temp
                            );

                            // add current account to accounts list
                            accounts.add(account);

                            // add current account's transactions to transactions list
                            if (account.getTransactions() != null && account.getTransactions().size() > 0) {
                                transactions.addAll(account.getTransactions());
                            }
                        }

                        // check if user has accounts
                        if (accounts.size() > 0) {
                            // create backup for user's list of accounts
                            accountsBackup.addAll(accounts);

                            // create backup for user's transactions per account
                            if (transactions.size() > 0) {
                                transactionsBackup.addAll(transactions);
                            }

                            // proceed to setting required data for summary statistics
                            setData();
                        }
                        // display message
                        else {
                            displayEmptyMessage();
                        }
                    } catch (Exception e) {
                        // display exception's message
                        e.printStackTrace();

                        displayEmptyMessage();

                        // set error text
                        tvEmpty.setText("Error occurred during the process.");
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    // leave blank...
                }
            });
        }
        // if invalid session
        else {
            // redirect to login page for user account login
            goBackToLogin();
        }
    }

    /**
     * Displays empty data message to user.
     */
    private void displayEmptyMessage() {
        // show message
        tvEmpty.setVisibility(View.VISIBLE);

        // hide pie chart graph
        pieChart.setVisibility(View.GONE);

        // hide summary statistics texts
        tvSummaryTitle.setVisibility(View.GONE);
        for (int i = 0; i < tvSummaryTexts.length; i++) {
            tvSummaryTexts[i].setVisibility(View.GONE);
        }

        // set preference box to unclickable
        ibPrefs.setClickable(false);
        ibPrefs.setEnabled(false);
    }

    /**
     * Displays empty data message to user on specified date.
     */
    private void displayEmptyDateMessage() {
        // show message
        tvDateEmpty.setVisibility(View.VISIBLE);

        // hide pie chart graph
        pieChart.setVisibility(View.GONE);

        // hide summary statistics texts
        tvSummaryTitle.setVisibility(View.GONE);
        for (int i = 0; i < tvSummaryTexts.length; i++) {
            tvSummaryTexts[i].setVisibility(View.GONE);
        }
    }

    /**
     * Launches an activity leading to the Login page and finishes this activity.
     */
    private void goBackToLogin () {
        Intent i = new Intent (this, LoginActivity.class);
        startActivity (i);
        finish ();
    }

    /**
     * Initializes and sets up the required data for the summary statistics.
     */
    private void setData() {
        /* TOP ACCOUNTS BY BALANCE DATA */
        // sort accounts by balance in descending order
        Collections.sort(accounts, new BalanceComparator());

        // accumulate total balance from all accounts of user
        double totalBalance = 0.0;
        for (Account account : accounts) {
            totalBalance += account.getBalance();
        }

        // for more than 3 accounts
        double otherAccountsBalance = 0.0;

        // initialize pie chart data entries for "top accounts by balance"
        ArrayList<PieEntry> pieAccounts = new ArrayList<>();
        // get the top 3 accounts by balance
        for (int i = 0; i < Math.min(accounts.size(), 3); i++) {
            pieAccounts.add(new PieEntry(
                    (float)(Math.round(accounts.get(i).getBalance() / totalBalance * 100) / 100.0 * 100),
                    accounts.get(i).getName()
            ));
        }
        // if user has more than 3 accounts, create pie data for the "other accounts"
        if (accounts.size() > 3) {
            // accumulate total for the "other accounts"
            for (int i = 3; i < accounts.size(); i++) {
                otherAccountsBalance += accounts.get(i).getBalance();
            }

            // add as pie data
            pieAccounts.add(new PieEntry(
                    (float)(Math.round(otherAccountsBalance / totalBalance * 100) / 100.0 * 100),
                    "Others"
            ));
        }

        // initialize accounts pie data set
        PieDataSet pieDataSetAccounts = new PieDataSet(pieAccounts, "Top Accounts By Balance");
        pieDataSetAccounts.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSetAccounts.setValueTextColor(Color.BLACK);
        pieDataSetAccounts.setValueTextSize(14f);

        // initialize accounts pie data for pie chart
        pieDataAccounts = new PieData(pieDataSetAccounts);
        pieDataAccounts.setValueFormatter(new PercentFormatter(pieChart));

        /* INCOME, INVESTMENT, AND EXPENSE RATIO DATA */
        // get number of income, investment, and expense transactions from each account
        int incomes = 0;
        int investments = 0;
        int expenses = 0;
        for (Transaction transaction : transactions) {
            switch (transaction.getType()) {
                case Transaction.TYPE_INCOME:
                    incomes++;
                    break;
                case Transaction.TYPE_INVESTMENT:
                    investments++;
                    break;
                case Transaction.TYPE_EXPENSE:
                    expenses++;
            }
        }

        // get total count of transactions
        int totalTransactions = incomes + investments + expenses;

        // incomes, investments, and expenses ratio percentages
        float p1 = (float)(Math.round(incomes * 1.0 / totalTransactions * 100));
        float p2 = (float)(Math.round(investments * 1.0 / totalTransactions * 100));
        float p3 = (float)(Math.round(expenses * 1.0 / totalTransactions * 100));

        // initialize pie chart data entries for "income, investment, and expense ratios"
        if (totalTransactions > 0) {
            ArrayList<PieEntry> pieRatios = new ArrayList<>();
            pieRatios.add(new PieEntry(p1, "Income"));
            pieRatios.add(new PieEntry(p2, "Investment"));
            pieRatios.add(new PieEntry(p3, "Expense"));

            // initialize pie data set for ratios
            PieDataSet pieDataSetRatios = new PieDataSet(pieRatios, "Income, Investment, and Expense Ratio");
            pieDataSetRatios.setColors(ColorTemplate.PASTEL_COLORS);
            pieDataSetRatios.setValueTextColor(Color.BLACK);
            pieDataSetRatios.setValueTextSize(14f);

            // initialize pie data for ratios pie chart
            pieDataRatios = new PieData(pieDataSetRatios);
            pieDataRatios.setValueFormatter(new PercentFormatter(pieChart));
        }

        // set pie chart to use percent values
        pieChart.setUsePercentValues(true);

        // initialize summary numerical texts
        String monthFilter = btnMonth.getText().toString().toLowerCase();
        String yearFilter = btnYear.getText().toString().toLowerCase();
        String date;
        if (!yearFilter.equalsIgnoreCase("none")) {
            date = yearFilter;
        } else {
            date = Calendar.getInstance().get(Calendar.YEAR) + "";
        }
        if (!monthFilter.equalsIgnoreCase("none")) {
            date = DateHelper.getMonthFormat(Integer.parseInt(transactions.get(0).getMonth()))
                    .substring(0, 3) + " " + date;
        }
        titleAccounts = "Top accounts by balance in " + date;
        titleRatios = "Income, Investment, and Expense Ratio in " + date;

        textAccounts.clear();
        for (int i = 0; i < Math.min(accounts.size(), 3); i++) {
            String accountName = accounts.get(i).getName();
            String percentage = (float)(Math.round(accounts.get(i).getBalance() / totalBalance * 100)) + "";
            textAccounts.add(accountName + " - " + percentage + "%");
        }
        if (accounts.size() > 3) {
            String percentage = (float)(Math.round(otherAccountsBalance / totalBalance * 100)) + "";
            textAccounts.add("Others - " + percentage + "%");
        }

        textRatios.clear();
        if (totalTransactions > 0) {
            String incomePercentage = p1 + " ";
            String investPercentage = p2 + " ";
            String expensePercentage = p3 + " ";
            textRatios.add("Income - " + incomePercentage + "%");
            textRatios.add("Investment - " + investPercentage + "%");
            textRatios.add("Expense - " + expensePercentage + "%");
        }

        // display data
        onSelectSummary();
    }

    /**
     * Initializes the Spinner component/s.
     */
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

    /**
     * Initializes the DatePicker components.
     */
    private void initDatePickers() {
        // initialize date picker components inside preferences / filter box
        btnMonth = findViewById (R.id.btn_summary_month_filter);
        btnYear = findViewById (R.id.btn_summary_year_filter);
        tvMonthLabel = findViewById(R.id.tv_summary_month_filter_label);
        tvYearLabel = findViewById(R.id.tv_summary_year_filter_label);

        System.out.println ("VERSION: " + android.os.Build.VERSION.SDK_INT);

        // For retrieving date today
        Calendar cal = Calendar.getInstance ();

        /* Month DatePickerDialog components */
        // Initialize Month DatePickerDialog (Filter for Month)
        dpMonthDialog = new DatePickerDialog(this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
            // On selecting a month, trigger filter
            @Override
            public void onDateSet (DatePicker view, int year, int month, int dayOfMonth) {
                // Update button text
                btnMonth.setText (DateHelper.getMonthFormat (month + 1));
                // Trigger filter
                filterData();
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
        dpYearDialog = new DatePickerDialog(this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
            // On selecting a year, trigger filter
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update button text
                btnYear.setText (String.valueOf(year));

                // Trigger filter
                filterData();
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

    /**
     * Clears the preferences / filter that the user chose and resets the summary statistics data
     * to be displayed.
     */
    private void clearPreferences() {
        // reset month and year buttons
        btnMonth.setText("NONE");
        btnYear.setText("NONE");

        // reset summary type selected value
        spSummaryType.setSelection(0);

        // reset account list data if needed
        if (accounts.size() != accountsBackup.size()) {
            accounts.clear();
            accounts.addAll(accountsBackup);
        }

        // reset transaction list data if needed
        if (transactions.size() != transactionsBackup.size()) {
            transactions.clear();
            transactions.addAll(transactionsBackup);
        }

        setData();

        // set default summary statistics
        onSelectSummary();
    }

    /**
     * Shows summary statistics components (pie chart, etc.).
     */
    private void showSummaryComponents() {
        // show pie chart
        pieChart.setVisibility(View.VISIBLE);

        // hide empty messages
        tvEmpty.setVisibility(View.GONE);
        tvDateEmpty.setVisibility(View.GONE);

        // hide summary statistics texts
        tvSummaryTitle.setVisibility(View.VISIBLE);
        for (int i = 0; i < tvSummaryTexts.length; i++) {
            tvSummaryTexts[i].setVisibility(View.VISIBLE);
        }
    }

    /**
     * Changes summary statistics to show depending on summary type chosen by user.
     */
    private void onSelectSummary() {
        // show summary components
        showSummaryComponents();

        // to check if there is data to show
        boolean verified = true;

        // show progress bar
        pbSummary.setVisibility(View.VISIBLE);

        // get selected summary type
        String type = spSummaryType.getSelectedItem().toString();

        if (type.equalsIgnoreCase("Top Accounts By Balance")) {
            // hide month and year filter options
            btnMonth.setVisibility(View.GONE);
            btnYear.setVisibility(View.GONE);
            tvMonthLabel.setVisibility(View.GONE);
            tvYearLabel.setVisibility(View.GONE);

            tvSummaryTitle.setText(titleAccounts);

            for (int i = 0; i < Math.min(accounts.size(), 3); i++) {
                tvSummaryTexts[i].setVisibility(View.VISIBLE);
                tvSummaryTexts[i].setText(textAccounts.get(i));
            }

            // if more than 3 accounts, display 4th summary statistics text
            if (accounts.size() > 3) {
                tvSummaryTexts[3].setVisibility(View.VISIBLE);
                tvSummaryTexts[3].setText(textAccounts.get(3));
            }

            // hide texts according to accounts size
            for (int i = accounts.size(); i < tvSummaryTexts.length; i++) {
                tvSummaryTexts[i].setVisibility(View.GONE);
            }

            // update pie chart with chosen data (income, investment, and expense ratio)
            pieChart.setData(pieDataAccounts);
            pieChart.setCenterText("Top Accounts By Balance");
        } else {
            // if there are data for transactions
            if (transactions.size() > 0) {
                // show month and year filter options
                btnMonth.setVisibility(View.VISIBLE);
                btnYear.setVisibility(View.VISIBLE);
                tvMonthLabel.setVisibility(View.VISIBLE);
                tvYearLabel.setVisibility(View.VISIBLE);

                tvSummaryTitle.setText(titleRatios);

                for (int i = 0; i < 3; i++) {
                    tvSummaryTexts[i].setText(textRatios.get(i));
                }

                tvSummaryTexts[3].setText("");
                tvSummaryTexts[3].setVisibility(View.GONE);

                // update pie chart with chosen data (income, investment, and expense ratio)
                pieChart.setData(pieDataRatios);
                pieChart.setCenterText("Income, Investment, and Expense Ratio");
            } else {
                verified = false;
            }
        }

        // hide progress bar
        pbSummary.setVisibility(View.GONE);

        // if there is data to show
        if (verified) {
            // set no description
            pieChart.getDescription().setEnabled(false);

            // refresh pie chart
            pieChart.invalidate();

            // spin pie chart
            pieChart.animate();
            pieChart.spin( 500,0,360f, Easing.EaseInOutQuad);
        }
        // specifically for income, investment, and expense ratio data, when no data to show
        else {
            displayEmptyDateMessage();
            tvDateEmpty.setText(
                    "You have no created transactions yet, create a transaction from one of your accounts."
            );
        }
    }

    /**
     * Filters the summary statistics data according to chosen month, year, or both.
     */
    private void filterData() {
        // get month and year filter values
        String monthFilter = btnMonth.getText().toString().toLowerCase();
        String yearFilter = btnYear.getText().toString().toLowerCase();

        // if no filter specified
        if (monthFilter.equalsIgnoreCase("none") &&
            yearFilter.equalsIgnoreCase("none")) {
            // reset transactions list data if needed
            if (transactions.size() != transactionsBackup.size()) {
                transactions.clear();
                transactions.addAll(transactionsBackup);
                setData();
            }
        }
        // filter according to month and year specified
        else {
            // create a temporary list (for copying purposes)
            ArrayList<Transaction> temp = new ArrayList<>();

            // to indicate if transactions has been filtered according to month
            boolean changed = false;

            // if the user filtered based on the MONTH of transactions
            if (!monthFilter.equalsIgnoreCase("none")) {
                // loop through each transaction
                for (Transaction transaction : transactionsBackup) {
                    // if month filter value matches the current transaction's month
                    if (monthFilter.equalsIgnoreCase (DateHelper.getMonthFormat (Integer.parseInt (transaction.getMonth ())))) {
                        // add to temporary list
                        temp.add(transaction);
                    }
                }

                // reset transactions list
                transactions.clear();
                // populate transactions list with filtered content
                transactions.addAll(temp);
                // clear temporary list
                temp.clear();

                // transactions has been filtered with month
                changed = true;
            }

            // If the user filtered based on the YEAR of Transactions
            if (!yearFilter.equalsIgnoreCase ("none")) {
                ArrayList<Transaction> temp2;
                if(changed) {
                    temp2 = transactions;
                } else {
                    temp2 = transactionsBackup;
                }

                // Loop through each Transaction
                for (Transaction transaction : temp2) {
                    // If "Year" filter matches the Year of the Transaction
                    if (yearFilter.equalsIgnoreCase (transaction.getYear ()))
                        // Add to temporary list
                        temp.add (transaction);
                }

                // Reset main list
                transactions.clear ();
                // Populate main list with filtered content
                transactions.addAll (temp);
                // Clear filtered list holder
                temp.clear ();
            }

            // if no transaction data acquired from the specified month and year filter
            // display empty date message
            if (transactions.size() <= 0) {
                displayEmptyDateMessage();
            }
            // else, set data with acquired filtered transactions data
            else {
                setData();
            }
        }
    }

    /**
     * Launches an activity leading to the Home page and finishes this activity.
     */
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