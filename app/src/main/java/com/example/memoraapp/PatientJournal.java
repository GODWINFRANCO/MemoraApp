package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientJournal extends AppCompatActivity {

    EditText date, journalTxt;
    Button send;
    FloatingActionButton image;
//    Spinner day;

    String sid,sname,sphone,semail,sdate,sday, sjournal,status,message,url= config.BaseUrl+"journal.php" ;
    String datee = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

//    String[] days = {"Select day", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_journal);

        date=findViewById(R.id.Adate);
//        day=findViewById(R.id.Aday);
        journalTxt =findViewById(R.id.ADiary);
        send=findViewById(R.id.btnSend);
        image=findViewById(R.id.image);

        HashMap<String, String> map = new SessionManager(PatientJournal.this).getUserDetails();
        sid = map.get("id");
//        sname = map.get("username");
//        sphone = map.get("phone_number");
//        semail = map.get("email");

//        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
//        day.setAdapter(dayAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PatientJournal.this,PatientJournalListActivity.class);
                startActivity(intent);
            }
        });
        date.setOnClickListener(v -> showDatePicker());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send1();
            }
        });
    }
    // Method to show Date Picker
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PatientJournal.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    date.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day
        );

        // 🔒 Disable past dates
        datePickerDialog.getDatePicker()
                .setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }
    private void send1() {
        sdate=date.getText().toString();
//        sday=day.getText().toString();
        sjournal = journalTxt.getText().toString();
//        sday= day.getSelectedItem().toString();


        if (TextUtils.isEmpty(sdate)){
            date.requestFocus();
            date.setError("Choose date");
            return;
        }

//        if (sday.equals("Select day")) {
//            Toast.makeText(this, "Please Select Day", Toast.LENGTH_SHORT).show();
//            day.requestFocus();
//            return;
//        }

//        if (TextUtils.isEmpty(sday)){
//            day.requestFocus();
//            day.setError("Enter day");
//            return;
//        }

        if (TextUtils.isEmpty(sjournal)){
            journalTxt.requestFocus();
            journalTxt.setError("Enter Journal");
            return;
        }

        StringRequest StringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(Register.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject c = new JSONObject(response);
                            status = c.getString("status");
                            message = c.getString("message");
                            checklogin();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //run cheyikkumbo error indo ennu nokkan
                        Toast.makeText(PatientJournal.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",sid);
                params.put("date",sdate);
                params.put("journal", sjournal);
                return params;

            }


        };

        //string reqt ne execute cheyan aanu requestqueue
        Volley volley =  null;
        RequestQueue requestQueue = volley.newRequestQueue(this);
        requestQueue.add(StringRequest);
    }


    private void checklogin() {
        if (status.equals("0")){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Dairy successfully", Toast.LENGTH_SHORT).show();
//            Intent i =new Intent(PatientJournal.this,Home.class);
//            startActivity(i);
            if (sjournal != null && !sjournal.isEmpty()) {

                // Convert to lowercase for case-insensitive checking
                String currentEntry = sjournal.toLowerCase();

                // Check for specific words
                if (currentEntry.contains("happy") || currentEntry.contains("good") || currentEntry.contains("great")) {
                    Toast.makeText(this, "Glad to hear you're feeling good!", Toast.LENGTH_LONG).show();
                }
                else if (currentEntry.contains("sad") || currentEntry.contains("alone")) {
                    Toast.makeText(this, "We are here for you. Stay strong!", Toast.LENGTH_LONG).show();
                }
                else if (currentEntry.contains("pain") || currentEntry.contains("headache") || currentEntry.contains("hurt")) {
                    Toast.makeText(this, "Please consult a doctor if the pain persists.", Toast.LENGTH_LONG).show();
                }
            }
            finish();
        }
    }
}