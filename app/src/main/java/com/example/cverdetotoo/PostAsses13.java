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

public class PostAsses13 extends AppCompatActivity {

    // Variable to track the score
    private int score = 0;

    // Timer-related variables
    private static final long TOTAL_TIME = 20000; // 20 seconds in milliseconds
    private CountDownTimer countDownTimer;
    private TextView timerTextView;

    // The correct answer is assumed to be prebtn1a
    private int correctAnswerId = R.id.prebtn1a;
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
        setContentView(R.layout.activity_post_asses13);

        // Retrieve score from previous activity if available
        score = getIntent().getIntExtra("score", 0);

        // Initialize views
        timerTextView = findViewById(R.id.posttimer13);
        radioGroup = findViewById(R.id.radiopostQ13);
        submitButton = findViewById(R.id.postq13);
        next11Button = findViewById(R.id.postnext13);

        // Initially disable the next button and lower its opacity to 50%
        next11Button.setEnabled(false);
        next11Button.setAlpha(0.5f);

        // Start the 20-second timer
        startTimer();

        // Set listener for the submit button click
        submitButton.setOnClickListener(v -> {
            if (!answered) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    // No answer selected
                    Toast.makeText(PostAsses13.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cancel the timer
                countDownTimer.cancel();

                // Reset the button tint of all radio buttons (in case user is retrying)
                resetRadioButtonColors(radioGroup);

                // Define custom darker colors
                int darkGreen = 0xFF00CC00;  // Dark green
                int darkRed = 0xFFCC0000;    // Dark red

                if (selectedId == correctAnswerId) {
                    // Correct answer: update only the button tint to dark green
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PostAsses13.this, "Correct!", Toast.LENGTH_SHORT).show();
                    score++;  // Increase score
                } else {
                    // Wrong answer: mark the selected radio button dark red (only the circle)
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkRed));

                    // Also highlight the correct answer in dark green (only the circle)
                    RadioButton correctRadioButton = findViewById(correctAnswerId);
                    correctRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PostAsses13.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                }

                // Mark as answered and disable further selections
                answered = true;
                disableRadioGroup();

                // Disable the submit button (set opacity to 50%)
                submitButton.setEnabled(false);
                submitButton.setAlpha(0.5f);

                // Enable the next button (set opacity to 100%)
                next11Button.setEnabled(true);
                next11Button.setAlpha(1.0f);
            }
        });

        // Set listener for the next button click to redirect to PostAsses14.class
        next11Button.setOnClickListener(v -> {
            if (answered) {
                Intent intent = new Intent(PostAsses13.this, PostAsses14.class);
                intent.putExtra("score", score);  // Passing the cumulative score to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(PostAsses13.this, "Please submit your answer first", Toast.LENGTH_SHORT).show();
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
                next11Button.setEnabled(true);
                next11Button.setAlpha(1.0f);
                Toast.makeText(PostAsses13.this, "Time is up! Correct answer is shown.", Toast.LENGTH_SHORT).show();

                // Define a custom dark green color
                int darkGreen = 0xFF00CC00;

                // Highlight the correct answer's circle in dark green
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
        // Cancel the timer to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}