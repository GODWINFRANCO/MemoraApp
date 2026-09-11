package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CaregiverReminder extends AppCompatActivity {

    private EditText medicineName;
    private EditText selectDate;
    private CheckBox dailyCheckBox;
    private TimePicker timePicker;
    private Button setReminderButton, viewReminders;
    private String url = config.BaseUrl + "reminder.php";
    String name, id, patient_id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_reminder);

        // Initialize views
        medicineName = findViewById(R.id.medicineName);
        selectDate = findViewById(R.id.selectDate);
        dailyCheckBox = findViewById(R.id.dailyCheckBox);
        timePicker = findViewById(R.id.timePicker);
        setReminderButton = findViewById(R.id.setReminderButton);
        viewReminders = findViewById(R.id.viewReminders);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> finish());

        HashMap<String,String> rev = new CaregiverSessionManager(CaregiverReminder.this).getUserDetails();
        name = rev.get("caregivername");
        id = rev.get("id");

        Intent intent = getIntent();
        patient_id = intent.getStringExtra("patient_id");

        selectDate.setOnClickListener(v -> showDatePickerDialog());
        setReminderButton.setOnClickListener(v -> setMedicineReminder());

        viewReminders.setOnClickListener(v -> {
            Intent i = new Intent(CaregiverReminder.this, CaregiverReminderListActivity.class);
            i.putExtra("patient_id", patient_id);
            startActivity(i);
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            selectDate.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void setMedicineReminder() {
        String medName = medicineName.getText().toString().trim();
        String selectDateValue = selectDate.getText().toString().trim();
        boolean isDaily = dailyCheckBox.isChecked();

        if(medName.isEmpty() || selectDateValue.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // CHECK PERMISSION FOR ANDROID 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Please allow exact alarms permission", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 1. Get Time correctly directly from picker
        int hour, minute;
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Calendar calendar = Calendar.getInstance();
        String[] dateParts = selectDateValue.split("/");
        if(dateParts.length == 3){
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If time has passed, move to next day/occurrence immediately
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 2. Create Intent
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicinename", medName);
        intent.putExtra("selectdate", selectDateValue);
        intent.putExtra("caregivername", name);
        intent.putExtra("is_daily", isDaily);
        intent.putExtra("time_hour", hour); // Save time for rescheduling
        intent.putExtra("time_minute", minute); // Save time for rescheduling

        // Generate Unique ID based on time to prevent overwriting
        int uniqueId = (int) System.currentTimeMillis();
        intent.putExtra("request_code", uniqueId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                uniqueId,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // 3. Set EXACT Alarm (Do not use setRepeating)
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
        }

        // Database logic
        saveToDatabase(medName, selectDateValue, hour, minute, isDaily);
    }

    private void saveToDatabase(String medName, String dateValue, int hour, int minute, boolean isDaily) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject c = new JSONObject(response);
                        // Optional: Handle success response
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(CaregiverReminder.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("caregiver_id", id);
                params.put("caregivername", name);
                params.put("patient_id", patient_id);
                params.put("reminder", medName);
                params.put("date", dateValue);
                params.put("time", String.format("%02d:%02d", hour, minute));
                params.put("frequency", isDaily ? "Daily" : "Once");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}