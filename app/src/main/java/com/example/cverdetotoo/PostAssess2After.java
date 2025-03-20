package com.example.cverdetotoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostAssess2After extends AppCompatActivity {

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
        setContentView(R.layout.activity_post_assess2_after);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnq1done = findViewById(R.id.post2done);
        scoreTextView = findViewById(R.id.post2score);
        resultTextView = findViewById(R.id.wtg);     // This TextView shows "Well Done!" or "Failed"
        wtgm1TextView = findViewById(R.id.wtgm1);      // This TextView will display the motivational message

        int score = getIntent().getIntExtra("score", 0);
        scoreTextView.setText(String.valueOf(score));

        // Determine result message based on the score
        String resultMessage = (score >= 4) ? "Well Done!" : "Failed";
        resultTextView.setText(resultMessage);

        // If failed, update the wtgm1 TextView with the motivational message
        if(resultMessage.equals("Failed")) {
            wtgm1TextView.setText("Don't worry! Every step toward learning about the environment makes a difference. Keep going, and you'll get there! Click 'Next' to watch an educational video and discover simple ways to reduce your carbon footprint.");
        }

        saveScoreToFirestore(score, resultMessage);

        btnq1done.setOnClickListener(v -> {
            markPreAssessmentCompleted(); // Mark assessment as completed in Firestore
            navigateToNavbar(score, resultMessage);
            if (score >= 4) {
                // If user passes, go to CertificateActivity
                Intent intent = new Intent(PostAssess2After.this, CertificateActivity.class);
                intent.putExtra("username", auth.getCurrentUser().getDisplayName());
                startActivity(intent);
            } else {
                // If failed, go to the navbar
                navigateToNavbar(score, resultMessage);
            }
        });
    }

    private void saveScoreToFirestore(int score, String result) {
        String username = auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : null;

        if (username != null) {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("score", score);
            scoreData.put("timestamp", System.currentTimeMillis());
            scoreData.put("isCompleted", true); // Mark quiz as completed
            scoreData.put("username", username);
            scoreData.put("result", result);      // Save the result message

            db.collection("PostAssess2")
                    .document(username)
                    .set(scoreData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PostAssess2After.this, "Score saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PostAssess2After.this, "Error saving score", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToNavbar(int score, String result) {
        Intent intent = new Intent(this, navbar.class);
        intent.putExtra("isCompleted", true);
        intent.putExtra("score", score);
        intent.putExtra("result", result); // Pass the result message to the next activity
        startActivity(intent);
        finish();
    }

    private void markPreAssessmentCompleted() {
        String username = auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : null;
        if (username != null) {
            DocumentReference docRef = db.collection("PostAssess2").document(username);
            docRef.update("isCompleted", true)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PostAssess2After.this, "Assessment marked as completed", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PostAssess2After.this, "Failed to update completion status", Toast.LENGTH_SHORT).show());
        }
    }
}
