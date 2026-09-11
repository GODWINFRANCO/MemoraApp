package com.example.memoraapp.ui.reminders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.memoraapp.R;
import com.example.memoraapp.ReminderAdapter;
import com.example.memoraapp.ReminderModel;
import com.example.memoraapp.SessionManager;
import com.example.memoraapp.config;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RemindersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<ReminderModel> reminderList;
    private ReminderAdapter adapter;

    // Replace with your actual PHP URL
    private String url = config.BaseUrl + "reminder_list.php";

    private String patientId;

    public RemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views using the 'view' object
        recyclerView = view.findViewById(R.id.reminder_recycler);
        progressBar = view.findViewById(R.id.reminder_progress);
//        MaterialToolbar topAppBar = view.findViewById(R.id.topAppBar);

        // Handle Back Navigation
//        topAppBar.setNavigationOnClickListener(v -> {
            // Uses the Activity's back press logic
//            requireActivity().OnBackPressedDispatcher.onBackPressed();
            // OR if strictly using fragment stack:
            // getParentFragmentManager().popBackStack();
//        });

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(requireContext(), reminderList);
        recyclerView.setAdapter(adapter);

        // Get Patient ID from Session
        HashMap<String, String> userDetails = new SessionManager(requireContext()).getUserDetails();
        patientId = userDetails.get("id");

        if (patientId != null) {
            fetchReminders();
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchReminders() {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Check if fragment is still attached to avoid crashes on view update
                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray array = new JSONArray(response);
                        reminderList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

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
                        Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_id", patientId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
}