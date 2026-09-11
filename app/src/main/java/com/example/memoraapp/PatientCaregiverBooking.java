package com.example.memoraapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientCaregiverBooking extends AppCompatActivity {

    ImageView pfp, certificate;
    TextView name, phone, address, fees;
    MaterialButton confirm;
    EditText date, notes;

    // Strings
    String patientPfp, caregiverPfp;
    String sname, sphone, saddress, sfees, sdate, snotes;
    String scid, spid, spname, spphone;
    String scertificate;

    String status, message;

    String url = config.BaseUrl + "patientcaregiverbooking.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_caregiver_booking);

        // UI Binding
        pfp = findViewById(R.id.cbpfp);
        name = findViewById(R.id.cbname);
        phone = findViewById(R.id.cbphone);
        address = findViewById(R.id.cbaddress);
        fees = findViewById(R.id.cbfee);
        date = findViewById(R.id.cbdate);
        notes = findViewById(R.id.cbnotes);
        confirm = findViewById(R.id.confirmBooking);
        certificate = findViewById(R.id.certificatePhoto);

        // Patient Session Data
        HashMap<String, String> data =
                new SessionManager(PatientCaregiverBooking.this).getUserDetails();

        spid = data.get("id");
        spname = data.get("patientname");
        spphone = data.get("patientphone");
        patientPfp = data.get("patientpfp");

        // Caregiver Data from Intent
        Intent in = getIntent();

        scid = in.getStringExtra("id");
        sname = in.getStringExtra("caregivername");
        sphone = in.getStringExtra("caregiverphone");
        saddress = in.getStringExtra("caregiveraddress");
        sfees = in.getStringExtra("caregiverfee");

        caregiverPfp = in.getStringExtra("caregiverpfp");
        scertificate = in.getStringExtra("certificate");

        // ✅ Load Caregiver Profile Image
        if (caregiverPfp != null && !caregiverPfp.isEmpty()) {
            Picasso.get()
                    .load(config.CaretakerprofileUrl + caregiverPfp)
                    .into(pfp);
        }

        // ✅ Load Certificate Image
        if (scertificate != null && !scertificate.isEmpty()) {
            Picasso.get()
                    .load(config.certificateUrl + scertificate)
                    .into(certificate);
        }

        // Set Text Data
        name.setText(sname);
        phone.setText(sphone);
        address.setText(saddress);
        fees.setText(sfees);

        // Insets Fix
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        // Date Picker
        date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) ->
                            date.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        });

        // Confirm Booking Click
        confirm.setOnClickListener(v -> confirmBooking());
    }

    // ✅ Confirm Booking Function
    private void confirmBooking() {

        snotes = notes.getText().toString();
        sdate = date.getText().toString();

        if (TextUtils.isEmpty(sdate)) {
            date.requestFocus();
            date.setError("Date is required");
            return;
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject c = new JSONObject(response);

                        status = c.getString("status");
                        message = c.getString("message");

                        checkBookingStatus();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this,
                        "Volley Error: " + error.toString(),
                        Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                // Caregiver Info
                params.put("caregiverid", scid);
                params.put("caregivername", sname);
                params.put("caregiverphone", sphone);
                params.put("caregiverfees", sfees);
                params.put("caregiverpfp", caregiverPfp);

                // Patient Info
                params.put("patientid", spid);
                params.put("patientname", spname);
                params.put("patientphone", spphone);
                params.put("patientpfp", patientPfp);

                // Booking Info
                params.put("bookingdate", sdate);
                params.put("patientnotes", snotes);

                return params;
            }
        };

        RequestQueue requestQueue =
                Volley.newRequestQueue(PatientCaregiverBooking.this);

        requestQueue.add(stringRequest);
    }

    // ✅ Check Booking Response
    private void checkBookingStatus() {

        if (status.equals("0")) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
