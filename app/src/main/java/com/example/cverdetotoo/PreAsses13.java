package com.example.cverdetotoo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PreAsses13 extends AppCompatActivity {
    // Variable to track the score
    private int score = 0;
    // Variable to store the selected answer text
    private String selectedAnswer = "";

    // Timer-related variables
    private static final long TOTAL_TIME = 20000; // 20 seconds in milliseconds
    private CountDownTimer countDownTimer;
    private TextView timerTextView;

    // The correct answer is assumed to be prebtn1b
    private int correctAnswerId = R.id.prebtn1b;
    private RadioGroup radioGroup;
    private Button submitButton, next11Button;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_pre_asses13);

        // Retrieve score from previous activity if available
        score = getIntent().getIntExtra("score", 0);

        // Initialize views
        radioGroup = findViewById(R.id.radiopreQ13);
        submitButton = findViewById(R.id.preq13);
        next11Button = findViewById(R.id.next13);
        timerTextView = findViewById(R.id.timer13); // Ensure this TextView is added in your XML layout

        // Disable the Next button until an answer is submitted
        next11Button.setEnabled(false);

        // Start the 20-second timer
        startTimer();

        // Set listener for the submit button click
        submitButton.setOnClickListener(v -> {
            if (!answered) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // No answer selected
                    Toast.makeText(PreAsses13.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Store the selected answer's text for later passing
                RadioButton selectedRadioButton = findViewById(selectedId);
                selectedAnswer = selectedRadioButton.getText().toString();

                // Cancel the timer
                countDownTimer.cancel();

                // Define custom colors
                int darkGreen = 0xFF00CC00;  // Dark green
                int darkRed = 0xFFCC0000;    // Dark red

                // Reset the button tint for all radio buttons
                resetRadioButtonColors(radioGroup);

                if (selectedId == correctAnswerId) {
                    // Correct answer: update the selected radio button circle to dark green
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PreAsses13.this, "Correct!", Toast.LENGTH_SHORT).show();
                    score++;  // Increase score
                } else {
                    // Wrong answer: update the selected radio button circle to dark red
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkRed));

                    // Also highlight the correct answer in dark green
                    RadioButton correctRadioButton = findViewById(correctAnswerId);
                    correctRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PreAsses13.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                }

                // Mark as answered, disable further selections, disable submit button, and enable Next button
                answered = true;
                disableRadioGroup();
                submitButton.setEnabled(false);
                next11Button.setEnabled(true);
            }
        });

        // Set listener for the Next button click to redirect to PreAsses14.class
        next11Button.setOnClickListener(v -> {
            if (answered) {
                Intent intent = new Intent(PreAsses13.this, PreAsses14.class);
                // Pass both the cumulative score and the selected answer to the next activity
                intent.putExtra("score", score);
                intent.putExtra("selectedAnswer", selectedAnswer);
                startActivity(intent);
            } else {
                Toast.makeText(PreAsses13.this, "Please submit your answer first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Starts the countdown timer for 20 seconds.
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                timerTextView.setText("Time left: " + secondsLeft + "s");
            }
            @Override
            public void onFinish() {
                timerTextView.setText("Time's up!");
                disableRadioGroup();
                // Automatically select and show the correct answer when time runs out
                radioGroup.check(correctAnswerId);
                answered = true;
                submitButton.setEnabled(false);
                next11Button.setEnabled(true); // Enable Next button on auto-submission
                Toast.makeText(PreAsses13.this, "Time is up! Correct answer is shown.", Toast.LENGTH_SHORT).show();

                int darkGreen = 0xFF00CC00;
                RadioButton correctRadioButton = findViewById(correctAnswerId);
                correctRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
            }
        }.start();
    }

    /**
     * Disables all RadioButtons in the RadioGroup to prevent further changes.
     */
    private void disableRadioGroup() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);
            child.setEnabled(false);
        }
    }

    /**
     * Resets the button tint of all RadioButtons in the RadioGroup to default.
     */
    private void resetRadioButtonColors(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            if (radioGroup.getChildAt(i) instanceof RadioButton) {
                RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
                rb.setButtonTintList(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}