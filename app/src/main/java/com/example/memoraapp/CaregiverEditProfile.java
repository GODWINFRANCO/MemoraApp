package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.memoraapp.databinding.ActivityCaregiverEditProfileBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CaregiverEditProfile extends AppCompatActivity {

    EditText name,phone,mail,pass,dob,address,fee;
    Button save;
    ImageView backbtn,addpfp,dispfp;
    String sname,sphone,smail,sfee,sid,spass,sdob,spfp,saddress,status,message,url=config.BaseUrl+"Caregivereditprofile.php";


    String imageUploadUrl=config.BaseUrl+"caregiverpfpupdate.php";
    private RequestQueue rQueue;
    private static ProgressDialog mProgressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_caregiver_edit_profile);

        rQueue = Volley.newRequestQueue(this);
        name=findViewById(R.id.ceditname);
        phone=findViewById(R.id.ceditphone);
        mail=findViewById(R.id.ceditmail);
        pass=findViewById(R.id.ceditpass);
        dob=findViewById(R.id.ceditdob);
        address=findViewById(R.id.ceditaddress);
        save=findViewById(R.id.ceditsave);
        backbtn=findViewById(R.id.ceditbackbtn);
        addpfp=findViewById(R.id.ceditaddimg);
        fee=findViewById(R.id.ceditfee);
        dispfp=findViewById(R.id.ceditprofileImage);

        //data get cheyyaan
        Intent in =getIntent();
        sid=in.getStringExtra("id");
        sname=in.getStringExtra("caregivername");
        sphone=in.getStringExtra("caregiverphone");
        saddress=in.getStringExtra("caregiveraddress");
        sfee=in.getStringExtra("caregiverfee");
        sdob=in.getStringExtra("caregiverdob");
        smail=in.getStringExtra("caregivermail");
        spass=in.getStringExtra("caregiverpass");
        spfp=in.getStringExtra("caregiverpfp");

        Picasso.get().load(config.CaretakerprofileUrl + spfp).into(dispfp);


        //data print cheyyaan
        name.setText(sname);
        phone.setText(sphone);
        address.setText(saddress);
        fee.setText(sfee);
        dob.setText(sdob);
        mail.setText(smail);
        pass.setText(spass);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addpfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();


            }


        });
        dob.setOnClickListener(v -> showDatePicker());

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Savechanges();
            }

        });
    }
    private void Savechanges() {

        sname = name.getText().toString();
        sphone = phone.getText().toString();
        sdob = dob.getText().toString();
        smail = mail.getText().toString();
        spass = pass.getText().toString();
        saddress=address.getText().toString();
        sfee=fee.getText().toString();

        if(TextUtils.isEmpty(sname)){
            name.requestFocus();
            name.setError("Name is required");
            return; }
        if(TextUtils.isEmpty(sphone)){
            phone.requestFocus();
            phone.setError("Phonenumber is required"); return; }
        if(TextUtils.isEmpty(sfee)){
            fee.requestFocus();
            fee.setError("Fee is required"); return; }
        if(TextUtils.isEmpty(saddress)){
            address.requestFocus();
            address.setError("Address is required"); return; }
        if(TextUtils.isEmpty(sdob)){
            dob.requestFocus();
            dob.setError("DOB is required");
            return; }
        if(TextUtils.isEmpty(smail)){
            mail.requestFocus();
            mail.setError("E-mail is required");
            return; }
        if(TextUtils.isEmpty(spass)){
            pass.requestFocus();
            pass.setError("Password is required");
            return; }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    //Toast.makeText(CaregiverEditProfile.this, response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject c = new JSONObject(response);
                        status = c.getString("status");
                        message = c.getString("message");
                        checksave();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(CaregiverEditProfile.this, error.toString(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", sid);

                params.put("caregivername", sname);
                params.put("caregiverphone", sphone);
                params.put("caregiveraddress", saddress);
                params.put("caregiverfee", sfee);
                params.put("caregiverdob", sdob);
                params.put("caregivermail", smail);
                params.put("caregiverpass", spass);
                params.put("caregiverpfp",spfp);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void checksave() {
        if (status.equals("0")){
            Toast.makeText(this, "Invalied", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            Intent i =new Intent(CaregiverEditProfile.this,CaregiverProfile.class);
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
                CaregiverEditProfile.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    dob.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void upload() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String displayName = null;

            if (uri.toString().startsWith("content://")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        uploadImage(displayName, uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
                }
            } else if (uri.toString().startsWith("file://")) {
                displayName = new File(uri.toString()).getName();
                uploadImage(displayName, uri);
            }
        }
    }

    private void uploadImage(final String imageName, Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] inputData = getBytes(inputStream);
            inputStream.close();

            showSimpleProgressDialog(this, null, "Uploading image...", false);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                    Request.Method.POST,
                    imageUploadUrl,
                    response -> {
                        removeSimpleProgressDialog();
                        String rawResponse = new String(response.data);
                        try {
                            JSONObject jsonObject = new JSONObject(rawResponse);
                            status = jsonObject.getString("status");
                            message = jsonObject.getString("message");

                            if ("1".equals(status)) {
                                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, CaregiverProfile.class));
                            } else {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        removeSimpleProgressDialog();
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Network error";
                        Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", sid);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("filename", new DataPart(imageName, inputData, "image/jpeg"));
                    return params;
                }
            };

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            rQueue.add(volleyMultipartRequest);

        } catch (IOException e) {
            e.printStackTrace();
            removeSimpleProgressDialog();
            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
        }
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

    public void showSimpleProgressDialog(Context context, String title, String msg, boolean isCancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(context, title, msg);
            mProgressDialog.setCancelable(isCancelable);
        }
    }

    public void removeSimpleProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
