package com.example.memoraapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Add_Memory_album extends AppCompatActivity {

    EditText edtMemoryText;
    Button btnAddMemory;

    static final int IMAGE_REQ = 1;
    static final int VIDEO_REQ = 2;
    static final int AUDIO_REQ = 3;

    String uploadType = "";
    String thumbnailBase64 = "";
    String patientId, caretakerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory_album);

        edtMemoryText = findViewById(R.id.edtMemoryText);
        btnAddMemory = findViewById(R.id.btnAddMemory);

        HashMap<String, String> data = new SessionManager(Add_Memory_album.this).getUserDetails();
        caretakerId = data.get("id");  caretakerId = data.get("id");
        patientId= data.get("pateint_id");





        btnAddMemory.setOnClickListener(v -> showOptions());
    }

    private void showOptions() {
        String[] options = {"Text + Image", "Text + Video", "Text + Audio"};

        new AlertDialog.Builder(this)
                .setTitle("Add Memory")
                .setItems(options, (d, i) -> {
                    if (i == 0) pickMedia("image/*", IMAGE_REQ, "image");
                    if (i == 1) pickMedia("video/*", VIDEO_REQ, "video");
                    if (i == 2) pickMedia("audio/*", AUDIO_REQ, "audio");
                })
                .show();
    }

    private void pickMedia(String type, int code, String mediaType) {
        uploadType = mediaType;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadMemory(data.getData());
        }
    }

    private void uploadMemory(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            String fileBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            String thumbnailBase64 = "";

            if (uploadType.equals("video")) {
                Bitmap thumb = createVideoThumbnail(uri);
                thumbnailBase64 = bitmapToBase64(thumb);
            }

            String finalThumbnailBase6 = thumbnailBase64;
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    config.BaseUrl + "add_memory.php",
                    response -> Toast.makeText(this, response, Toast.LENGTH_LONG).show(),
                    error -> Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("patient_id", patientId);
                    map.put("caretaker_id", caretakerId);
                    map.put("text", edtMemoryText.getText().toString());
                    map.put("media_type", uploadType);
                    map.put("file", fileBase64);

                    if (uploadType.equals("video")) {
                        map.put("thumbnail", finalThumbnailBase6);
                    }

                    return map;
                }
            };

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap createVideoThumbnail(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bmp = null;

        try {
            retriever.setDataSource(this, uri);
            bmp = retriever.getFrameAtTime(1000000); // 1 second
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}