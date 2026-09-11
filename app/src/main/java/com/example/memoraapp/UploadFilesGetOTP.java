package com.example.memoraapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
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
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UploadFilesGetOTP extends AppCompatActivity {
    Button otp;
    String otpnew,status,message,id,name;
    String url = config.BaseUrl +"updateotp.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_files_get_otp);
        otp=findViewById(R.id.opendigi);

        HashMap<String,String> user=new SessionManager(UploadFilesGetOTP.this).getUserDetails();
        id=user.get("id");
        name=user.get("patientphone");


        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(v -> {
            finish();   // goes back to previous activity
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getotp1();
            }
        });
    }
    private void getotp1() {

        Random r = new Random();
        int otp = r.nextInt((9999 - 1000) + 1) + 1000;
        otpnew = "" + otp;

        StringRequest string=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity(),response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);
                    status = json.getString("status");
                    message = json.getString("message");


                    if (status.equals("1")) {
                        SmsManager sms=SmsManager.getDefault();
                        Intent in = new Intent();
                        PendingIntent pi = PendingIntent.getActivity(UploadFilesGetOTP.this, 0, in, PendingIntent.FLAG_IMMUTABLE);
                        sms.sendTextMessage(String.valueOf(name),null,"Your One Time Password for accessing digilocker has been generated.Your OTP is " + otpnew, pi,null);
                        Toast.makeText(UploadFilesGetOTP.this, "Otp Send SMS successfully", Toast.LENGTH_SHORT).show();
                        Intent object=new Intent(UploadFilesGetOTP.this, DigiLockerUploads.class); // -->Digilocker.class
                        startActivity(object);
                        // Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(UploadFilesGetOTP.this,message, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadFilesGetOTP.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("id",id);
                map.put("otp",otpnew);

                return map;
            }
        };
        RequestQueue req= Volley.newRequestQueue(UploadFilesGetOTP.this);
        req.add(string);
    }
}



