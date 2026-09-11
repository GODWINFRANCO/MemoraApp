package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CaregiverRegister extends AppCompatActivity {
    EditText email,password,name,dob,phonenum,address,fee;
    TextView login;
    ImageView UploadCertificate;

    Button registerbutton;

    RadioGroup regen;
    String gender;
    String semail,sname,sphonenum,saddress,spassword,sdob,sfee,status,message,url=config.BaseUrl+"Caregiverregister.php";

    String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

    Uri selectedImageUri = null;
    private static ProgressDialog mProgressDialog;
    RequestQueue rQueue;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_caregiver_register);
        email=findViewById(R.id.cregmail);
        password=findViewById(R.id.cregpassword);
        name=findViewById(R.id.cregname);
        dob=findViewById(R.id.cregdob);
        phonenum=findViewById(R.id.cregphonenum);
        login=findViewById(R.id.creglogin);
        regen = findViewById(R.id.cgen);
        address=findViewById(R.id.cregaddr);
        registerbutton=findViewById(R.id.cregregisterbutton);
        fee=findViewById(R.id.cregfee);

        UploadCertificate=findViewById(R.id.uploadCertificate);

        UploadCertificate.setOnClickListener(v -> pickImage());



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dob.setOnClickListener(v -> showDatePicker());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(CaregiverRegister.this, CaregiverLogin.class);
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

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CaregiverRegister.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    dob.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
    private void pickImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), 101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            UploadCertificate.setImageURI(selectedImageUri);
        }
    }
    private void register() {

        sname = name.getText().toString();
        sphonenum = phonenum.getText().toString();
        sdob = dob.getText().toString();
        semail = email.getText().toString();
        spassword = password.getText().toString();
        saddress=address.getText().toString();
        sfee=fee.getText().toString();

        if(TextUtils.isEmpty(sname)){
            name.requestFocus();
            name.setError("Name is required");
            return; }
        if(TextUtils.isEmpty(sphonenum)){
            phonenum.requestFocus();
            phonenum.setError("Phonenumber is required");
            return; }
        if(TextUtils.isEmpty(sdob)){
            dob.requestFocus();
            dob.setError("DOB is required");
            return; }
        if(TextUtils.isEmpty(saddress)){
            address.requestFocus();
            address.setError("Address is required"); return; }
        if(TextUtils.isEmpty(sfee)){
            fee.requestFocus();
            fee.setError("Fee is required");
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

        if (selectedImageUri == null) {
            Toast.makeText(this, "Upload certificate!", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog(this, "Uploading...");


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, url,
                response -> {
                    hideProgressDialog();
                    try {
                        JSONObject obj = new JSONObject(new String(response.data));
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if (obj.getString("status").equals("1")) {
                            startActivity(new Intent(CaregiverRegister.this, CaregiverLogin.class));
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CaregiverRegister.this, CaregiverLogin.class));
                        finish();
                    }
                },
                error -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("caregivername", sname);
                params.put("caregiverphone", sphonenum);
                params.put("caregiveraddress",saddress);
                params.put("caregiverfee",sfee);
                params.put("caregiverdob", sdob);
                params.put("caregivermail", semail);
                params.put("caregiverpass", spassword);
                params.put("caregivergender", gender);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {

                Map<String, DataPart> params = new HashMap<>();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] fileData = getBytes(inputStream);

                    params.put("certificate", new DataPart(
                            "certi_" + System.currentTimeMillis() + ".jpg",
                            fileData,
                            "image/jpeg"
                    ));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rQueue = Volley.newRequestQueue(this);
        rQueue.add(volleyMultipartRequest);
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;

        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
    private void showProgressDialog(Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    //Toast.makeText(CaregiverRegister.this, response, Toast.LENGTH_SHORT).show();
//                    try {
//                        JSONObject c = new JSONObject(response);
//                        status = c.getString("status");
//                        message = c.getString("message");
//                        checklogin();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                },
//                error -> Toast.makeText(CaregiverRegister.this, error.toString(), Toast.LENGTH_SHORT).show()
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("caregivername", sname);
//                params.put("caregiverphone", sphonenum);
//                params.put("caregiveraddress",saddress);
//                params.put("caregiverfee",sfee);
//                params.put("caregiverdob", sdob);
//                params.put("caregivermail", semail);
//                params.put("caregiverpass", spassword);
//                params.put("caregivergender", gender);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }



//    private void checklogin() {
//        if (status.equals("0")){
//            Toast.makeText(this, "Invalied", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
//            Intent i =new Intent(CaregiverRegister.this,CaregiverLogin.class);
//            startActivity(i);
//            finish();
//        }
//
//
//
//    }

//}

