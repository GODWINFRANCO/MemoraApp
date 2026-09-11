package com.example.memoraapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PatientFamiliarPhotoTest extends AppCompatActivity {

    ImageView photo;
    EditText answer;
    Button checktn;
    ProgressBar progress_bar;
    TextView ai_result;

    String retrievedNote = "";
    String user_id, userAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_familiar_photo_test);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        photo = findViewById(R.id.photo);
        answer = findViewById(R.id.answer);
        checktn = findViewById(R.id.checktn);
        progress_bar = findViewById(R.id.progress_bar);
        ai_result = findViewById(R.id.ai_result);

        HashMap<String, String> map = new SessionManager(this).getUserDetails();
        user_id = map.get("id");

        fetchPhoto();

        checktn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAI();
            }
        });
    }

    private void checkAI() {
        userAnswer = answer.getText().toString().trim();

        // 1. Basic Validation
        //if (retrievedNote == null || retrievedNote.isEmpty()) {
           // Toast.makeText(this, "Please wait for the photo to load.", Toast.LENGTH_SHORT).show();
          //  return;
       // }

        if (userAnswer.isEmpty()) {
            answer.setError("Please type a name or guess.");
            return;
        }

        // 2. Clear previous result and show loading
        progress_bar.setVisibility(View.VISIBLE);
        ai_result.setText("Analyzing answer...");
        ai_result.setTextColor(getResources().getColor(android.R.color.black));

        // 3. Send EVERYTHING to AI.
        // We do NOT use equalsIgnoreCase here. We let the AI decide context.
        evaluateAnswerWithAI(retrievedNote, userAnswer);
    }

    private void evaluateAnswerWithAI(String correctNote, String patientGuess) {
        String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

        JSONObject requestBody = new JSONObject();
        try {
            // Use a model capable of following JSON instructions well
            requestBody.put("model", config.MODEL); // Or use config.MODEL
            requestBody.put("temperature", 0.5); // Lower temp for more logical answers
            requestBody.put("max_tokens", 200);

            // Construct the Prompt
            JSONArray messages = new JSONArray();

            // System Message: Define the persona and strict JSON requirement
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a compassionate memory care assistant. " +
                    "Your task is to compare a User's Guess against the Correct Photo Description. " +
                    "1. If the guess is semantically similar (e.g., Correct: 'Grandson Billy', Guess: 'Billy' or 'My grandson'), mark it as correct. " +
                    "2. If the guess is wrong, be gentle. " +
                    "3. YOU MUST RESPONSE IN RAW JSON FORMAT ONLY: { \"is_correct\": boolean, \"message\": \"string\" }");
            messages.put(systemMessage);

            // User Message: The data to compare
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            String promptText = String.format(
                    "Correct Description: '%s'\nUser Guess: '%s'\n\nReturn the JSON object.",
                    correctNote, patientGuess
            );

            userMessage.put("content", promptText);
            messages.put(userMessage);

            requestBody.put("messages", messages);

            // Force JSON object mode (supported by some Groq models, helps reliability)
            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_object");
            requestBody.put("response_format", responseFormat);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress_bar.setVisibility(View.GONE);
                            try {
                                JSONArray choices = response.getJSONArray("choices");
                                JSONObject choice = choices.getJSONObject(0);
                                JSONObject message = choice.getJSONObject("message");
                                String rawContent = message.getString("content").trim();

                                // Parse the JSON returned by AI
                                JSONObject aiResponseJson = new JSONObject(rawContent);
                                boolean isCorrect = aiResponseJson.getBoolean("is_correct");
                                String displayMessage = aiResponseJson.getString("message");

                                ai_result.setText(displayMessage);

                                if (isCorrect) {
                                    ai_result.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                } else {
                                    ai_result.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                ai_result.setText("AI analysis error. Please try again.");
                                Log.e("AI_PARSE", "Error: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress_bar.setVisibility(View.GONE);
                            String errorMsg = "Unknown error";
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                errorMsg = new String(error.networkResponse.data);
                            }
                            Log.e("AI_API", "Error: " + errorMsg);
                            ai_result.setText("Connection failed. Please check internet.");
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + config.key); // Ensure this key is in your config
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Add retry policy for slower connections
            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(PatientFamiliarPhotoTest.this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            progress_bar.setVisibility(View.GONE);
        }
    }

    private void fetchPhoto() {
        progress_bar.setVisibility(View.VISIBLE);
        String url = config.BaseUrl + "photo_single.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress_bar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            if (status.equals("1")) {
                                String imageUrl = jsonObject.getString("imagetxt");
                                retrievedNote = jsonObject.getString("note");

                                String fullImageUrl = config.photos + imageUrl;
                                Picasso.get().load(fullImageUrl).into(photo);

                                // Reset UI for new photo
                                ai_result.setText("");
                                ai_result.setTextColor(getResources().getColor(android.R.color.black));
                                answer.setText("");

                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(PatientFamiliarPhotoTest.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PatientFamiliarPhotoTest.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(PatientFamiliarPhotoTest.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}