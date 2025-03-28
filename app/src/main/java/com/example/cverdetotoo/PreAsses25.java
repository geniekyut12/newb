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

public class PreAsses25 extends AppCompatActivity {
    // Variable to track the score
    private int score = 0;

    // Timer-related variables
    private static final long TOTAL_TIME = 20000; // 20 seconds in milliseconds
    private CountDownTimer countDownTimer;
    private TextView timerTextView;

    // The correct answer is assumed to be prebtn1b
    private int correctAnswerId = R.id.prebtn1b;
    private RadioGroup radioGroup;
    private Button submitButton, nextButton;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_pre_asses25);

        // Retrieve score from previous activity if available
        score = getIntent().getIntExtra("score", 0);

        // Initialize views
        radioGroup = findViewById(R.id.radiopreQ25);
        submitButton = findViewById(R.id.preq25);
        nextButton = findViewById(R.id.next25);
        timerTextView = findViewById(R.id.timer25); // Ensure this TextView is in your layout

        // Disable the next button until an answer is submitted
        nextButton.setEnabled(false);
        nextButton.setAlpha(0.5f);

        // Start the 20-second timer
        startTimer();

        // Set listener for the submit button click
        submitButton.setOnClickListener(v -> {
            if (!answered) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // No answer selected
                    Toast.makeText(PreAsses25.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cancel the timer
                countDownTimer.cancel();

                // Reset the button tint for all radio buttons
                resetRadioButtonColors();

                // Define custom colors
                int darkGreen = 0xFF00CC00;  // Dark green
                int darkRed = 0xFFCC0000;    // Dark red

                if (selectedId == correctAnswerId) {
                    // Correct answer: highlight the selected radio button circle with dark green
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PreAsses25.this, "Correct!", Toast.LENGTH_SHORT).show();
                    score++;  // Increase score
                } else {
                    // Wrong answer: highlight the selected radio button circle with dark red
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkRed));

                    // Also highlight the correct answer in dark green
                    RadioButton correctRadioButton = findViewById(correctAnswerId);
                    correctRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PreAsses25.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                }

                // Mark as answered, disable further selections, and enable next button
                answered = true;
                disableRadioGroup();
                submitButton.setEnabled(false);
                submitButton.setAlpha(0.5f);
                nextButton.setEnabled(true);
                nextButton.setAlpha(1.0f);
            }
        });

        // Set listener for the next button click to redirect to PreAssess2after.class
        nextButton.setOnClickListener(v -> {
            if (answered) {
                Intent intent = new Intent(PreAsses25.this, PreAssess2after.class);
                intent.putExtra("score", score);  // Passing the cumulative score to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(PreAsses25.this, "Please submit your answer first", Toast.LENGTH_SHORT).show();
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
                submitButton.setAlpha(0.5f);
                nextButton.setEnabled(true);
                nextButton.setAlpha(1.0f);
                Toast.makeText(PreAsses25.this, "Time is up! Correct answer is shown.", Toast.LENGTH_SHORT).show();

                // Highlight the correct answer in dark green
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
    private void resetRadioButtonColors() {
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