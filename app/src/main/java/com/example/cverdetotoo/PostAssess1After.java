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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostAssess1After extends AppCompatActivity {

    private Button btnq1done;
    private TextView scoreTextView, resultTextView, wtgm1TextView; // wtgm1TextView for motivational message
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the action bar (header)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_post_assess1_after);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnq1done = findViewById(R.id.post1done);
        scoreTextView = findViewById(R.id.post1score);
        resultTextView = findViewById(R.id.wtg);       // Displays the result message ("Well Done!" or "Failed")
        wtgm1TextView = findViewById(R.id.wtgm1);        // Displays a motivational message if needed

        // Declare variables as final so they can be used inside lambdas
        final int score = getIntent().getIntExtra("score", 0);
        scoreTextView.setText(String.valueOf(score));

        final String resultMessage = (score >= 4) ? "Well Done!" : "Failed";
        resultTextView.setText(resultMessage);

        // If the user failed, update wtgm1TextView with a motivational message
        if (resultMessage.equals("Failed")) {
            wtgm1TextView.setText("Don't worry! Every step toward learning about the environment makes a difference. Keep going, and you'll get there! Click 'Next' to watch an educational video and discover simple ways to reduce your carbon footprint.");
        }

        // Save the score data to Firestore regardless of pass or fail
        saveScoreToFirestore(score, resultMessage);

        // Combined onClick listener
        // Combined onClick listener
        btnq1done.setOnClickListener(v -> {
            markPreAssessmentCompleted(); // Mark assessment as completed in Firestore

            if (score >= 4) {
                // Create a final variable for the username to use inside the lambda
                final String usernameFinal = (auth.getCurrentUser() != null &&
                        auth.getCurrentUser().getDisplayName() != null &&
                        !auth.getCurrentUser().getDisplayName().isEmpty())
                        ? auth.getCurrentUser().getDisplayName()
                        : auth.getCurrentUser().getUid();

                db.collection("certificate").document(usernameFinal)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Certificate already exists; redirect to navbar
                                Toast.makeText(PostAssess1After.this, "Certificate already received", Toast.LENGTH_SHORT).show();
                                navigateToNavbar(score, resultMessage);
                            } else {
                                // Certificate not received; go to CertificateActivity to show/generate it
                                Intent intent = new Intent(PostAssess1After.this, CertificateActivity.class);
                                intent.putExtra("username", usernameFinal);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // In case of error, default to navbar
                            Toast.makeText(PostAssess1After.this, "Error checking certificate, redirecting...", Toast.LENGTH_SHORT).show();
                            navigateToNavbar(score, resultMessage);
                        });
            } else {
                // If failed, go directly to the navbar
                navigateToNavbar(score, resultMessage);
            }
        });

    }

    private void saveScoreToFirestore(int score, String result) {
        String username = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getDisplayName() : null;
        if (username != null) {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("score", score);
            scoreData.put("timestamp", System.currentTimeMillis());
            scoreData.put("isCompleted", true); // Mark quiz as completed
            scoreData.put("username", username);
            scoreData.put("result", result);      // Store result message

            db.collection("PostAssess")
                    .document(username)
                    .set(scoreData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PostAssess1After.this, "Score saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PostAssess1After.this, "Error saving score", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToNavbar(int score, String result) {
        Intent intent = new Intent(this, navbar.class);
        intent.putExtra("isCompleted", true);
        intent.putExtra("score", score);
        intent.putExtra("result", result); // Pass the result message
        startActivity(intent);
        finish();
    }

    private void markPreAssessmentCompleted() {
        String username = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getDisplayName() : null;
        if (username != null) {
            DocumentReference docRef = db.collection("PostAssess").document(username);
            docRef.update("isCompleted", true)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(PostAssess1After.this, "Assessment marked as completed", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(PostAssess1After.this, "Failed to update completion status", Toast.LENGTH_SHORT).show());
        }
    }
}
