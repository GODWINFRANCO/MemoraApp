package com.example.memoraapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.memoraapp.R;
import com.example.memoraapp.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DigiLockerUploads extends AppCompatActivity {
    EditText e1;
    Button b1;
    String otp;

    MaterialToolbar topAppBar;
    String User,Pass,status,message,URL= config.BaseUrl +"checkotp.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digi_locker_uploads);


        e1 = findViewById(R.id.enterotp);
        b1 = findViewById(R.id.uploads);
        HashMap<String, String> user = new SessionManager(DigiLockerUploads.this).getUserDetails();
//        id=user.get("id");
        User = user.get("patientphone");

        topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> {
            finish();   // goes back to previous activity
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });
    }

    private void Submit() {
        otp=e1.getText().toString();

        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject js = new JSONObject(response);
                    status = js.getString("status");
                    message = js.getString("message");

                    if (status.equals("0")) {
                        Toast.makeText(DigiLockerUploads.this, "Access failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DigiLockerUploads.this, "Access success", Toast.LENGTH_LONG).show();
                        // new SessionManager(OtpActivity.this).createLoginSession(id, admission_no, name, address1, gender, age, dob, email,   department, year, mobile_no, password1, image);
                        Intent object=new Intent(DigiLockerUploads.this,UploadFiles.class);
                        startActivity(object);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DigiLockerUploads.this, error.toString(), Toast.LENGTH_SHORT).show();
            }

        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> ma = new HashMap<>();
                ma.put("otp", otp);
                ma.put("phonenumber",User);
                return ma;
            }

        };
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(sr);


    }
}
