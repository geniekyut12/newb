package com.example.cverdetotoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PreAssess2after extends AppCompatActivity {

    private Button btnq1done;
    private TextView scoreTextView, resultTextView, wtgm1TextView; // Added wtgm1TextView for motivational message
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_pre_assess2after);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnq1done = findViewById(R.id.pre2done);
        scoreTextView = findViewById(R.id.pre2score);
        resultTextView = findViewById(R.id.wtg);
        wtgm1TextView = findViewById(R.id.wtgm); // Make sure this exists in your XML layout

        int score = getIntent().getIntExtra("score", 0);
        scoreTextView.setText(String.valueOf(score));

        // Determine pass/fail message
        String resultMessage = (score >= 4) ? "Well Done!" : "Failed";
        resultTextView.setText(resultMessage);

        // If failed, update wtgm1 TextView with a motivational message
        if (resultMessage.equals("Failed")) {
            wtgm1TextView.setText("Don't worry! Every step toward learning about the environment makes a difference. Keep going, and you'll get there! Click 'Next' to watch an educational video and discover simple ways to reduce your carbon footprint.");
        }

        saveScoreToFirestore(score, resultMessage);

        btnq1done.setOnClickListener(v -> {
            if (validateBeforeRedirect()) {
                navigateToVideo(score, resultMessage);
            } else {
                Toast.makeText(PreAssess2after.this, "Validation failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveScoreToFirestore(int score, String result) {
        String username = auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : null;

        if (username != null) {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("score", score);
            scoreData.put("timestamp", System.currentTimeMillis());
            scoreData.put("username", username);
            scoreData.put("result", result);

            db.collection("PreAssess2")
                    .document(username)
                    .set(scoreData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PreAssess2after.this, "Score saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PreAssess2after.this, "Error saving score", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateBeforeRedirect() {
        return true; // Update with your actual validation logic if needed
    }

    private void navigateToVideo(int score, String result) {
        Intent intent = new Intent(this, video1.class);
        intent.putExtra("isQuizDone", true);
        intent.putExtra("score", score);
        intent.putExtra("result", result);
        startActivity(intent);
        finish();
    }
}
