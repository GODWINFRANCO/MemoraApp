package com.example.memoraapp.ui.journal;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.memoraapp.Notifii;
import com.example.memoraapp.PatientJournalListActivity;
import com.example.memoraapp.R;
import com.example.memoraapp.SessionManager;
import com.example.memoraapp.config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JournalFragment extends Fragment {

    EditText date, journalTxt;
    Button send;
    ImageButton btnMic;
    FloatingActionButton image;

    String sid, sname, sdate, sjournal, strigger, spfp, status, message, alertStatus;
    String url = config.BaseUrl + "journal.php";

    // Speech Result Launcher
    private final ActivityResultLauncher<Intent> speechResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            ArrayList<String> resultData =
                                    result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                            if (resultData != null && !resultData.isEmpty()) {
                                String spokenText = resultData.get(0);
                                String currentText = journalTxt.getText().toString();

                                journalTxt.setText(
                                        TextUtils.isEmpty(currentText)
                                                ? spokenText
                                                : currentText + " " + spokenText
                                );
                            }
                        }
                    });

    // Permission Launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            startSpeechToText();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Permission denied to record audio",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    public JournalFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        date = view.findViewById(R.id.Adate);
        journalTxt = view.findViewById(R.id.ADiary);
        send = view.findViewById(R.id.btnSend);
        image = view.findViewById(R.id.image);
        btnMic = view.findViewById(R.id.btnMic);

        HashMap<String, String> map =
                new SessionManager(requireContext()).getUserDetails();

        sid = map.get("id");
        sname = map.get("patientname");
        spfp = map.get("patientpfp");

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });

        image.setOnClickListener(v -> {
            Intent intent =
                    new Intent(requireContext(), PatientJournalListActivity.class);
            startActivity(intent);
        });

        date.setOnClickListener(v -> showDatePicker());
        send.setOnClickListener(v -> send1());

        btnMic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {

                startSpeechToText();
            } else {
                requestPermissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO);
            }
        });
    }

    private void startSpeechToText() {
        Intent intent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak your journal entry...");

        try {
            speechResultLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Speech input not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(requireContext(),
                        (view, selectedYear,
                         selectedMonth,
                         selectedDay) ->
                                date.setText(selectedDay + "/"
                                        + (selectedMonth + 1)
                                        + "/" + selectedYear),
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.add(Calendar.DAY_OF_YEAR, -7);

        datePickerDialog.getDatePicker()
                .setMinDate(minCalendar.getTimeInMillis());

        datePickerDialog.getDatePicker()
                .setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void send1() {

        sdate = date.getText().toString();
        sjournal = journalTxt.getText().toString();

        if (TextUtils.isEmpty(sdate)) {
            date.setError("Choose date");
            return;
        }

        if (TextUtils.isEmpty(sjournal)) {
            journalTxt.setError("Enter Journal");
            return;
        }

        StringRequest request =
                new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject obj = new JSONObject(response);
                                status = obj.getString("status");
                                message = obj.getString("message");
                                checklogin();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> Toast.makeText(requireContext(),
                                error.toString(),
                                Toast.LENGTH_SHORT).show()) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params =
                                new HashMap<>();
                        params.put("user_id", sid);
                        params.put("date", sdate);
                        params.put("journal", sjournal);
                        return params;
                    }
                };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void checklogin() {

        if ("0".equals(status)) {
            Toast.makeText(requireContext(),
                    "Error saving diary",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(),
                "Diary saved successfully",
                Toast.LENGTH_SHORT).show();

        if (sjournal != null && !sjournal.isEmpty()) {

            String currentEntry = sjournal.toLowerCase();

            String[] hazardousWords = {
                    "lost", "help", "emergency", "scared",
                    "trapped", "danger", "fell", "fall",
                    "blood", "hurt", "pain", "suicide",
                    "kill", "die", "don't know where"
            };

            boolean foundTrigger = false;

            for (String word : hazardousWords) {
                if (currentEntry.contains(word)) {
                    strigger = word;
                    foundTrigger = true;
                    break;
                }
            }

            if (foundTrigger) {
                sendDistress();
            } else {
                clearFields();
            }
        } else {
            clearFields();
        }
    }

    private void sendDistress() {

        StringRequest request =
                new StringRequest(Request.Method.POST,
                        config.BaseUrl + "alert_caregiver.php",
                        response -> {
                            try {
                                JSONObject obj = new JSONObject(response);
                                alertStatus = obj.getString("status");
                                String c_id = obj.getString("caregiver_id");

                                if ("1".equals(alertStatus)
                                        && !"0".equals(c_id)) {

                                    Toast.makeText(requireContext(),
                                            "Caregiver notified",
                                            Toast.LENGTH_SHORT).show();

                                    Notifii notification =
                                            new Notifii(requireContext());

                                    notification.showBookedNotification(
                                            requireContext(),
                                            sname,
                                            strigger
                                    );
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            clearFields();
                        },
                        error -> {
                            Toast.makeText(requireContext(),
                                    "Failed to send alert",
                                    Toast.LENGTH_SHORT).show();
                            clearFields();
                        }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params =
                                new HashMap<>();
                        params.put("user_id", sid);
                        params.put("patientname", sname);
                        params.put("date", sdate);
                        params.put("trigger", strigger);
                        params.put("patientpfp", spfp);
                        return params;
                    }
                };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void clearFields() {
        date.setText("");
        journalTxt.setText("");
        date.setError(null);
        journalTxt.setError(null);
    }
}
