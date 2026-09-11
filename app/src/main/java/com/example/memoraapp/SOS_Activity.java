package com.example.memoraapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SOS_Activity extends AppCompatActivity {

    ImageView sos_button;
    String user_id;
    String status, message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sos);

        HashMap<String, String> data = new SessionManager(SOS_Activity.this).getUserDetails();
        user_id = data.get("id");

        sos_button = findViewById(R.id.sos_button);
        sos_button.setOnClickListener(view -> sendSMSToAllContacts());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> {
            finish();   // goes back to previous activity
        });
    }
    private void sendSMSToAllContacts() {
        // Send request to get all registered student phone numbers
        StringRequest stringRequest = new StringRequest(Request.Method.POST, config.BaseUrl + "sos.php",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        status = jsonObject.getString("status");
                        message = jsonObject.getString("message");

                        if (status.equals("1")) {

                            JSONArray phoneNumbersArray = jsonObject.getJSONArray("contacts");

                            for (int i = 0; i < phoneNumbersArray.length(); i++) {
                                String phoneNumber = phoneNumbersArray.getString(i);
                                sendSMS(phoneNumber);
                            }

                            Toast.makeText(SOS_Activity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(SOS_Activity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SOS_Activity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SOS_Activity.this, "Error fetching phone numbers "+message, Toast.LENGTH_SHORT).show()
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void sendSMS (String phoneNumber){
        String message = "Urgent: I need immediate assistance. Please respond as soon as possible to help ensure my safety.";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}