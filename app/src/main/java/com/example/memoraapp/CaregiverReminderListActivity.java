package com.example.memoraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CaregiverReminderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<ReminderModel> reminderList;
    private CaregiverReminderAdapter adapter;

    // Replace with your actual PHP URL
    private String url = config.BaseUrl + "reminder_list.php";

    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list); // Ensure you have this layout file with a RecyclerView

        // Initialize Views
        recyclerView = findViewById(R.id.reminder_recycler);
        progressBar = findViewById(R.id.reminder_progress);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderList = new ArrayList<>();
        adapter = new CaregiverReminderAdapter(this, reminderList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        patientId = intent.getStringExtra("patient_id");

        // If the ID is empty (maybe logged in as caregiver?), handle logic here.
        // For now, we assume we want to load data for this ID.
        if (patientId != null) {
            fetchReminders();
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchReminders() {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray array = new JSONArray(response);
                        reminderList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            // Make sure these match the keys in your PHP script
                            reminderList.add(new ReminderModel(
                                    obj.getString("id"),
                                    obj.getString("caregiver_id"),
                                    obj.getString("caregivername"),
                                    obj.getString("patient_id"),
                                    obj.getString("reminder"),
                                    obj.getString("date"),
                                    obj.getString("time")
                            ));
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CaregiverReminderListActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CaregiverReminderListActivity.this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Sending patient_id to the server to filter the list
                params.put("patient_id", patientId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}