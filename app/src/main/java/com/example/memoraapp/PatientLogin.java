package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PatientLogin extends AppCompatActivity {


    EditText email,password;
    Button loginbutton;
    TextView register, forgotText;

   String semail,spassword,url=config.BaseUrl+"patientlogin.php",status,message,id,name,phone,address,dob,mail,pass,gender,pfp;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_login);
        email=findViewById(R.id.ploginemail);
        password=findViewById(R.id.ploginpassword);
        loginbutton=findViewById(R.id.ploginbutton);
        register=findViewById(R.id.ploginregister);
        forgotText = findViewById(R.id.forgotText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(PatientLogin.this, PatientRegister.class);
                startActivity(in);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();
            }


        });
        forgotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PatientLogin.this, PatientForgotPassword.class);
                startActivity(in);
            }
        });


    }

    private void login() {

        semail=email.getText().toString();
        spassword=password.getText().toString();

        if (TextUtils.isEmpty(semail)){
            email.requestFocus();
            email.setError("E-Mail id is required");
            return;
        }
        if (TextUtils.isEmpty(spassword)){
            password.requestFocus();
            password.setError("Password is required");
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
                            id=c.getString("id");
                            name=c.getString("patientname");
                            phone=c.getString("patientphone");
                            address=c.getString("patientaddress");
                            dob=c.getString("patientdob");
                            mail=c.getString("patientmail");
                            pass=c.getString("patientpass");
                            gender=c.getString("patientgender");
                            pfp=c.getString("patientpfp");


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
                        Toast.makeText(PatientLogin.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("patientmail", semail );
                params.put("patientpass", spassword);

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
            Toast.makeText(this, "Invalied", Toast.LENGTH_SHORT).show();
        }else {
            new SessionManager(PatientLogin.this).createLoginSession(id,name,phone,address,dob,mail,pass,gender,pfp);
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            Intent i =new Intent(PatientLogin.this,Home.class);
            startActivity(i);
            finish();
        }

    }
}

