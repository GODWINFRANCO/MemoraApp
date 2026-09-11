package com.example.memoraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.Markwon;

public class  ChatBotActivity extends AppCompatActivity {

    TextInputEditText userMessage;
    MaterialButton sendButton;
    TextView chatTextView;
    ScrollView chatScrollView;
    Markwon markwon;
    String user_id, status, message;
    String url = config.BaseUrl+"save_chat_summary.php";

    private JSONArray chatHistory = new JSONArray();
    String groqApiKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        markwon = Markwon.create(this);
        userMessage = findViewById(R.id.userMessage);
        sendButton = findViewById(R.id.sendButtonAI);
        chatTextView = findViewById(R.id.chatTextView);
        chatScrollView = findViewById(R.id.chatScrollView);

//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//        topAppBar.setNavigationOnClickListener(v -> finish());

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_finish) {
                analyzeAndCloseSession();
                return true;
            }
            return false;
        });

        HashMap<String,String>map=new SessionManager(ChatBotActivity.this).getUserDetails();
        user_id=map.get("id");

        // 1. Setup instructions
        setupSystemPrompt();

        // 2. TRIGGER THE FIRST QUESTION
        // We call the API with just the system prompt so it introduces itself
        updateChatDisplay("_Caregiver is joining the chat..._");
        callGroqAPI();

        sendButton.setOnClickListener(v -> {
            String message = userMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addToHistory("user", message);
                updateChatDisplay("**Patient:** " + message);
                callGroqAPI();
                userMessage.setText("");
            }
        });
    }

    private void setupSystemPrompt() {
        try {
            JSONObject systemObj = new JSONObject();
            systemObj.put("role", "system");
            systemObj.put("content", "You are a professional caregiver AI. " +
                    "Start the conversation by asking the patient how they are feeling today. " +
                    "Your goal is to detect the patient's emotion and physical well-being. " +
                    "Always end your response with a supportive question. Keep responses concise. Ask questions step by step. One at a time");
            chatHistory.put(systemObj);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addToHistory(String role, String content) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("role", role);
            msg.put("content", content);
            chatHistory.put(msg);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateChatDisplay(String newMarkdownText) {
        String currentContent = chatTextView.getText().toString();
        // If it's the very first message (the loading hint), don't add extra newlines
        String updatedContent = currentContent.isEmpty() ? newMarkdownText : currentContent + "\n\n" + newMarkdownText;
        markwon.setMarkdown(chatTextView, updatedContent);
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void callGroqAPI() {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String botReply = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        addToHistory("assistant", botReply);

                        // Clean the display if it still shows the "joining" hint
                        if(chatTextView.getText().toString().contains("Caregiver is joining")) {
                            chatTextView.setText("");
                            updateChatDisplay("**Caregiver AI:** " + botReply.trim());
                        } else {
                            updateChatDisplay("**Caregiver AI:** " + botReply.trim());
                        }

                    } catch (Exception e) {
                        updateChatDisplay("_Error parsing response._");
                    }
                },
                error -> {
                    String errorMsg = "Error: " + error.toString();
                    updateChatDisplay("_" + errorMsg + "_");
                }) {

            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("model", "openai/gpt-oss-20b");
                    jsonBody.put("messages", chatHistory);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + groqApiKey);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void analyzeAndCloseSession() {
        updateChatDisplay("\n_Analyzing session for Caretaker..._");

        String url = "https://api.groq.com/openai/v1/chat/completions";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String summary = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        // 2. UPLOAD SUMMARY TO PHP/MYSQL
                        uploadSummaryToBackend(summary);

                    } catch (Exception e) {
                        updateChatDisplay("_Error analyzing session._");
                    }
                },
                error -> updateChatDisplay("_Error: " + error.toString() + "_")) {

            @Override
            public byte[] getBody() {
                try {
                    // Create a specific prompt for analysis
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("model", "openai/gpt-oss-20b"); // or llama3-8b-8192

                    JSONArray messages = new JSONArray();

                    // SYSTEM: Define the analyst persona
                    JSONObject sys = new JSONObject();
                    sys.put("role", "system");
                    sys.put("content", "You are a medical analyst. Read the following conversation history. " +
                            "Output EXACTLY ONE sentence summarizing the patient's dominant emotion and main complaint. " +
                            "Format: 'Emotion: [Emotion] - Summary: [Summary]'");
                    messages.put(sys);

                    // USER: Pass the entire chat history as the 'content' to analyze
                    JSONObject historyObj = new JSONObject();
                    historyObj.put("role", "user");
                    historyObj.put("content", "Chat History: " + chatHistory.toString());
                    messages.put(historyObj);

                    jsonBody.put("messages", messages);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) { return null; }
            }

            // ... headers and auth ...
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + groqApiKey);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    // 3. UPLOAD TO PHP
    private void uploadSummaryToBackend(String analysisResult) {




        StringRequest StringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(Register.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject c = new JSONObject(response);
                            status = c.getString("status");
                            message = c.getString("message");
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
                        Toast.makeText(ChatBotActivity.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }

                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("patient_id", user_id); // Replace with actual dynamic ID
                params.put("summary", analysisResult);
                params.put("timestamp", String.valueOf(System.currentTimeMillis()));
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
            Toast.makeText(this, "Invalid, "+ message, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
//            Intent i =new Intent(ChatBotActivity.this,Home.class);
//            startActivity(i);
            finish();
        }
    }




//        StringRequest request = new StringRequest(Request.Method.POST, phpUploadUrl,
//                response -> {
//                    // Success!
//                    Toast.makeText(this, "Session Saved Successfully", Toast.LENGTH_LONG).show();
//                    finish(); // Close the activity and go back
//                },
//                error -> {
//                    Toast.makeText(this, "Upload Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("patient_id", user_id); // Replace with actual dynamic ID
//                params.put("summary", analysisResult);
//                params.put("timestamp", String.valueOf(System.currentTimeMillis()));
//                return params;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(request);
}