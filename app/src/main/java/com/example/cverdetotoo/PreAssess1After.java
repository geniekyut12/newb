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

public class PreAssess1After extends AppCompatActivity {

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
        setContentView(R.layout.activity_pre_assess1_after);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnq1done = findViewById(R.id.pre1done);
        scoreTextView = findViewById(R.id.pre1score);
        resultTextView = findViewById(R.id.wtg);   // Displays "Well Done!" or "Failed"
        wtgm1TextView = findViewById(R.id.wtgm1);    // Displays the motivational message if the user failed

        wtgm1TextView.setSingleLine(false);
        wtgm1TextView.setMaxLines(Integer.MAX_VALUE); // Allow as many lines as needed
        wtgm1TextView.setEllipsize(null);

        int score = getIntent().getIntExtra("score", 0);
        scoreTextView.setText(String.valueOf(score));

        // Determine result message
        String resultMessage = (score >= 4) ? "Well Done!" : "Failed";
        resultTextView.setText(resultMessage);

        // If the user failed, update wtgm1 with the motivational message
        if(resultMessage.equals("Failed")){
            wtgm1TextView.setText("Don't worry! Every step \ntoward learning about the environment makes a difference.\n Keep going, and you'll get there! \nClick 'Next' to watch an educational video and discover simple\n ways to reduce your carbon footprint.");
        }

        saveScoreToFirestore(score, resultMessage);

        btnq1done.setOnClickListener(v -> {
            markPreAssessmentCompleted(); // Store completion in Firestore
            navigateToNavbar(score, resultMessage);
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
            scoreData.put("result", result);      // Store result message

            db.collection("PreAssess")
                    .document(username)
                    .set(scoreData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PreAssess1After.this, "Score saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PreAssess1After.this, "Error saving score", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToNavbar(int score, String result) {
        Intent intent = new Intent(this, video1.class);
        intent.putExtra("isCompleted", true);
        intent.putExtra("score", score);
        intent.putExtra("result", result); // Pass result message
        startActivity(intent);
        finish();
    }

    private void markPreAssessmentCompleted() {
        String username = auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : null;
        if (username != null) {
            DocumentReference docRef = db.collection("PreAssess").document(username);
            docRef.update("isCompleted", true)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PreAssess1After.this, "Assessment marked as completed", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PreAssess1After.this, "Failed to update completion status", Toast.LENGTH_SHORT).show());
        }
    }
}
