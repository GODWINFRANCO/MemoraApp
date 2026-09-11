package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PatientEditProfile extends AppCompatActivity {

    EditText name, phone, mail, pass, dob, address;
    Button save;
    ImageView backbtn, dispfp, addpfp;
    String sname, spfp, sphone, smail, sid, spass, sdob, saddress, status, message;
    String url = config.BaseUrl + "patienteditprofile.php";
    String imageUploadUrl = config.BaseUrl + "patientpfpupdate.php";

    private RequestQueue rQueue; // Initialized in onCreate
    private static ProgressDialog mProgressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_edit_profile);

        // Initialize Volley RequestQueue
        rQueue = Volley.newRequestQueue(this);

        // Initialize views
        name = findViewById(R.id.peditname);
        phone = findViewById(R.id.peditphone);
        mail = findViewById(R.id.peditmail);
        pass = findViewById(R.id.peditpass);
        dob = findViewById(R.id.peditdob);
        address = findViewById(R.id.peditaddress);
        save = findViewById(R.id.peditsave);
        backbtn = findViewById(R.id.peditbackbtn);
        dispfp = findViewById(R.id.peditprofileImage);
        addpfp = findViewById(R.id.peditaddimg);

        // Get intent data
        Intent in = getIntent();
        sid = in.getStringExtra("id");
        sname = in.getStringExtra("patientname");
        sphone = in.getStringExtra("patientphone");
        saddress = in.getStringExtra("patientaddress");
        sdob = in.getStringExtra("patientdob");
        smail = in.getStringExtra("patientmail");
        spass = in.getStringExtra("patientpass");
        spfp = in.getStringExtra("patientpfp");

        // Load profile picture
        if (spfp != null && !spfp.isEmpty()) {
            Picasso.get().load(config.PatientprofileUrl + spfp).into(dispfp);
        }

        // Set existing data
        name.setText(sname);
        phone.setText(sphone);
        address.setText(saddress);
        dob.setText(sdob);
        mail.setText(smail);
        pass.setText(spass);

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dob.setOnClickListener(v -> showDatePicker());

        backbtn.setOnClickListener(v -> {
//            startActivity(new Intent(PatientEditProfile.this, PatientProfile.class));
            finish();
        });

        save.setOnClickListener(v -> Savechanges());

        addpfp.setOnClickListener(v -> selectImageFromGallery());
    }

    // ---------- Save Profile Changes ----------
    private void Savechanges() {
        sname = name.getText().toString();
        sphone = phone.getText().toString();
        sdob = dob.getText().toString();
        smail = mail.getText().toString();
        spass = pass.getText().toString();
        saddress = address.getText().toString();

        // Validation
        if (TextUtils.isEmpty(sname)) { name.setError("Name is required"); return; }
        if (TextUtils.isEmpty(sphone)) { phone.setError("Phone number is required"); return; }
        if (TextUtils.isEmpty(saddress)) { address.setError("Address is required"); return; }
        if (TextUtils.isEmpty(sdob)) { dob.setError("DOB is required"); return; }
        if (TextUtils.isEmpty(smail)) { mail.setError("E-mail is required"); return; }
        if (TextUtils.isEmpty(spass)) { pass.setError("Password is required"); return; }

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject c = new JSONObject(new String(response.data));
                        status = c.getString("status");
                        message = c.getString("message");
                        if ("1".equals(status)) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, PatientProfile.class));
                            finish();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", sid);
                params.put("patientname", sname);
                params.put("patientphone", sphone);
                params.put("patientaddress", saddress);
                params.put("patientdob", sdob);
                params.put("patientmail", smail);
                params.put("patientpass", spass);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                return null; // No files in profile edit
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rQueue.add(request);
    }

    // ---------- Image Selection ----------
    private void selectImageFromGallery() {
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

    // ---------- Upload Image ----------
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
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            status = jsonObject.getString("status");
                            message = jsonObject.getString("message");

                            if ("1".equals(status)) {
                                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                Picasso.get().load(config.PatientprofileUrl + imageName).into(dispfp);
                            } else {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        removeSimpleProgressDialog();
                        Toast.makeText(this, "Upload error: " + (error.getMessage() != null ? error.getMessage() : "Network error"), Toast.LENGTH_LONG).show();
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

            // ✅ Add to initialized RequestQueue
            rQueue.add(volleyMultipartRequest);

        } catch (IOException e) {
            e.printStackTrace();
            removeSimpleProgressDialog();
            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- Utility ----------
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

    // ---------- Date Picker ----------
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PatientEditProfile.this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        dob.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day
        );
        datePickerDialog.show();
    }
}
