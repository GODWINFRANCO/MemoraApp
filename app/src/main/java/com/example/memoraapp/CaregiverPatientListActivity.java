package com.example.memoraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CaregiverPatientListActivity extends AppCompatActivity {

    String sphn;
    private String url = config.BaseUrl + "patient_list.php";
    private ArrayList<BookingRequestListModel> dataModelArrayList;
    private CaregiverPatientListAdapter rvvAdapter;
    private RecyclerView recyclerView;
    private ProgressBar p;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_request_list);

        recyclerView = findViewById(R.id.cycle1231xbook);
        p = findViewById(R.id.bar123xbook);

        HashMap<String, String> rev = new CaregiverSessionManager(CaregiverPatientListActivity.this).getUserDetails();
        sphn = rev.get("id");

        fetchingJSON();
    }

    private void fetchingJSON() {

        p.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        p.setVisibility(View.GONE);

                        dataModelArrayList = new ArrayList<>();
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataobj = array.getJSONObject(i);

                            dataModelArrayList.add(new BookingRequestListModel(
                                    dataobj.getString("id"),
                                    dataobj.getString("patientid"),
                                    dataobj.getString("patientname"),
                                    dataobj.getString("patientphone"),
                                    dataobj.getString("patientpfp"),
                                    dataobj.getString("caregiverid"),
                                    dataobj.getString("caregivername"),
                                    dataobj.getString("caregiverphone"),
                                    dataobj.getString("caregiverpfp"),
                                    dataobj.getString("caregiverfees"),
                                    dataobj.getString("bookingdate"),
                                    dataobj.getString("patientnotes"),
                                    dataobj.getString("created_at"),
                                    dataobj.getString("status"),
                                    dataobj.getString("fee"),
                                    dataobj.getString("feedate")
                            ));
                        }
                        setupRecycler();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    p.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("caregiverid", sphn);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override public int getCurrentTimeout() { return 20000; }
            @Override public int getCurrentRetryCount() { return 20000; }
            @Override public void retry(VolleyError error) {
                p.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupRecycler() {
        rvvAdapter = new CaregiverPatientListAdapter(this, dataModelArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(rvvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
}
