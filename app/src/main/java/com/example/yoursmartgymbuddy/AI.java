package com.example.yoursmartgymbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AI extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText userInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        // Retrieve the API key from strings.xml
        String openaiApiKey = getString(R.string.openai_api_key);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);

        // Set up chat adapter
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Send button listener
        sendButton.setOnClickListener(v -> {
            String input = userInput.getText().toString().trim();
            if (!input.isEmpty()) {
                addMessage(input, ChatMessage.SENT_BY_USER);
                userInput.setText("");
                sendToOpenAI(input, openaiApiKey);
            }
        });

        setupBottomNav();
    }

    private void addMessage(String message, int sender) {
        runOnUiThread(() -> {
            messages.add(new ChatMessage(message, sender));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        });
    }

    private void sendToOpenAI(String prompt, String apiKey) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String bodyJson = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"user\", \"content\": \"" + prompt + "\"}\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(bodyJson, JSON);
        Request request = new Request.Builder()
                .url(OPENAI_ENDPOINT)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                addMessage("Failed: " + e.getMessage(), ChatMessage.SENT_BY_BOT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject obj = new JSONObject(result);
                        JSONArray choices = obj.getJSONArray("choices");
                        String reply = choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addMessage(reply.trim(), ChatMessage.SENT_BY_BOT);
                    } catch (JSONException e) {
                        addMessage("JSON parse error: " + e.getMessage(), ChatMessage.SENT_BY_BOT);
                    }
                } else {
                    try {
                        String result = response.body().string();
                        addMessage("API error: " + result, ChatMessage.SENT_BY_BOT);  // Log the actual response body
                    } catch (IOException e) {
                        addMessage("Error reading response: " + e.getMessage(), ChatMessage.SENT_BY_BOT);  // Handle IO errors
                    }
                }
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.bottom_ai);

        bottomNav.setOnItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();
            if (id == R.id.bottom_Todo) intent = new Intent(this, ToDoList.class);
            else if (id == R.id.bottom_home) intent = new Intent(this, MainActivity.class);
            else if (id == R.id.bottom_account) intent = new Intent(this, AccountSettings.class);
            else if (id == R.id.bottom_maps) intent = new Intent(this, Maps.class);

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }
}
