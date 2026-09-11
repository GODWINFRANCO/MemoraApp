package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientRegister extends AppCompatActivity {
    EditText email,password,name,dob,phonenum,address;
    TextView login;

    Button registerbutton;

    RadioGroup regen;
    String gender;
    String semail,sname,sphonenum,spassword,sdob,saddress,status,message,url=config.BaseUrl+"patientregister.php";


    String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_register);
        email=findViewById(R.id.pregmail);
        password=findViewById(R.id.pregpassword);
        name=findViewById(R.id.pregname);
        dob=findViewById(R.id.pregdob);
        phonenum=findViewById(R.id.pregphonenum);
        login=findViewById(R.id.preglogin);
        regen = findViewById(R.id.pgen);
        registerbutton=findViewById(R.id.pregregisterbutton);
        address=findViewById(R.id.pregaddr);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dob.setOnClickListener(v -> showDatePicker());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(PatientRegister.this, PatientLogin.class);
                startActivity(in);
            }

        });


        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }


        });


    }


    private void register() {

        sname = name.getText().toString();
        sphonenum = phonenum.getText().toString();
        sdob = dob.getText().toString();
        semail = email.getText().toString();
        spassword = password.getText().toString();
        saddress=address.getText().toString();
        if(TextUtils.isEmpty(sname)){
            name.requestFocus();
            name.setError("Name is required");
            return; }
        if(TextUtils.isEmpty(sphonenum)){
            phonenum.requestFocus();
            phonenum.setError("Phonenumber is required"); return; }
        if(TextUtils.isEmpty(saddress)){
            address.requestFocus();
            address.setError("Address is required"); return; }
        if(TextUtils.isEmpty(sdob)){
            dob.requestFocus();
            dob.setError("DOB is required");
            return; }
        if(TextUtils.isEmpty(semail)){
            email.requestFocus();
            email.setError("E-mail is required");
            return; }
        if(TextUtils.isEmpty(spassword)){
            password.requestFocus();
            password.setError("Password is required");
            return; }
        int id = regen.getCheckedRadioButtonId();
        if (id == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioButton = findViewById(id);
        gender = radioButton.getText().toString();

        // your other validations ...

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                   // Toast.makeText(PatientRegister.this, response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject c = new JSONObject(response);
                        status = c.getString("status");
                        message = c.getString("message");
                        checklogin();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(PatientRegister.this, error.toString(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patientname", sname);
                params.put("patientphone", sphonenum);
                params.put("patientaddress", saddress);
                params.put("patientdob", sdob);
                params.put("patientmail", semail);
                params.put("patientpass", spassword);
                params.put("patientgender", gender);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void checklogin() {
        if (status.equals("0")){
            Toast.makeText(this, "Invalied", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            Intent i =new Intent(PatientRegister.this,PatientLogin.class);
            startActivity(i);
            finish();
        }



    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PatientRegister.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    dob.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day
        );
        datePickerDialog.show();


    }
}




