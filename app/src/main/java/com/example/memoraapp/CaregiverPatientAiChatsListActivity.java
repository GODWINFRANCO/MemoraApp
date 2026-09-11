package com.example.memoraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CaregiverPatientAiChatsListActivity extends AppCompatActivity {

    // FloatingActionButton readd;

    EditText search;
    Button edit,readd;
    String uid,sphn;

    private String url = config.BaseUrl+"caregiver_patient_ai_list.php";
    private ArrayList<CaregiverPatientAiChatsListModel> dataModelArrayList;
    private CaregiverPatientAiChatsListAdapter rvvAdapter;
    private RecyclerView recyclerView;
    private ProgressBar p;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_patient_ai_chats_list);

        recyclerView = findViewById(R.id.cycle1231book);
        p = findViewById(R.id.bar123book);

        Intent intent = getIntent();
        sphn = intent.getStringExtra("patientid");

        //readd=findViewById(R.id.addpetbook);


////        edit=findViewById(R.id.campbutton);
//        HashMap<String,String>rev=new CaregiverSessionManager(CaregiverListActivity.this).getUserDetails();
//
//        sphn=rev.get("phonenumber");


//        readd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(CaregiverListActivity.this, "Book your Caregiver", Toast.LENGTH_SHORT).show();
//
//                 Intent in=new Intent(CaregiverListActivity.this, PatientCaregiverBooking.class);
//                startActivity(in);
//            }
//        });


        //  edit=findViewById(R.id.prfbutton);


//        search=findViewById(R.id.search123);
//        search.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//            @Override
//            public void afterTextChanged(Editable text) {
//                //new array list that will hold the filtered data
//                ArrayList<addyourpetmodel> filteredSongs = new ArrayList<>();
//
//                if (dataModelArrayList != null && !dataModelArrayList.isEmpty()) {
//                    //looping through existing elements
//                    for (addyourpetmodel  s: dataModelArrayList) {
//                        //if the existing elements contains the search input
//                        if (s.getPetname().toLowerCase().contains(text.toString().toLowerCase())) {
//                            //adding the element to filtered list
//                            filteredSongs.add(s);
//                        }
//                    }
//                }
//
//                if (rvvAdapter != null) {
//                    //calling a method of the adapter class and passing the filtered list
//                    rvvAdapter.filterList(filteredSongs);
//                }
//            }
//        });

        fetchingJSON();
    }



    private void fetchingJSON() {

        p.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                        try {
                            p.setVisibility(View.GONE);

                            dataModelArrayList = new ArrayList<>();
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject dataobj = array.getJSONObject(i);

                                dataModelArrayList.add(new CaregiverPatientAiChatsListModel(
                                        dataobj.getString("id"),
                                        dataobj.getString("patient_id"),
                                        dataobj.getString("session_summary"),
                                        dataobj.getString("created_at")

                                ));
                            }
                            setupRecycler();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })

        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", sphn);
                return params;
            }


        };



        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 20000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 20000;
            }

            @Override
            public void retry(VolleyError error) {
                p.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void setupRecycler(){
        rvvAdapter = new CaregiverPatientAiChatsListAdapter(this, dataModelArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(rvvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

}
