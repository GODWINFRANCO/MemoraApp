package com.example.memoraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookingRequest_Full_activity extends AppCompatActivity {

    TextView patientname, bookingdate, patientnotes, created_at;
    MaterialButton btnApprove, btnReject;
    ImageView patientpfp;

    String sid, currentStatus;
    String url = config.BaseUrl + "bookingrequestfullactivity.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_request_full);

        patientname = findViewById(R.id.patientname);
        bookingdate = findViewById(R.id.bookingdate);
        patientnotes = findViewById(R.id.patientnotes);
        created_at = findViewById(R.id.created_at);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        patientpfp = findViewById(R.id.cbpfp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent in = getIntent();
        sid = in.getStringExtra("id");
        currentStatus = in.getStringExtra("status");

        patientname.setText(in.getStringExtra("patientname"));
        bookingdate.setText(in.getStringExtra("bookingdate"));
        patientnotes.setText(in.getStringExtra("patientnotes"));
        created_at.setText(in.getStringExtra("created_at"));

        String pfp = in.getStringExtra("patientpfp");
        if (pfp != null && !pfp.isEmpty()) {
            Picasso.get().load(config.PatientprofileUrl + pfp).into(patientpfp);
        }

        btnApprove.setOnClickListener(v -> updateStatus("approved"));
        btnReject.setOnClickListener(v -> updateStatus("rejected"));
    }

    private void updateStatus(String statusValue) {
        if (!currentStatus.equalsIgnoreCase("pending")) {
            Toast.makeText(this, "Already set the status", Toast.LENGTH_SHORT).show();
            return;
        }
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if (obj.getString("status").equals("1")) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", sid);
                params.put("status", statusValue);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
