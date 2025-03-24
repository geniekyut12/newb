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

public class PostAsses15 extends AppCompatActivity {

    // Variable to track the score
    private int score = 0;

    // Timer-related variables
    private static final long TOTAL_TIME = 20000; // 20 seconds
    private CountDownTimer countDownTimer;
    private TextView timerTextView;

    // The correct answer is assumed to be prebtn1c
    private int correctAnswerId = R.id.prebtn1c;
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
        setContentView(R.layout.activity_post_asses15);

        // Retrieve score from previous activity if available
        score = getIntent().getIntExtra("score", 0);

        // Initialize views
        timerTextView = findViewById(R.id.posttimer15);
        radioGroup = findViewById(R.id.radiopostQ15);
        submitButton = findViewById(R.id.postq15);
        nextButton = findViewById(R.id.postnext15);

        // Initially disable the next button and set its opacity to 50%
        nextButton.setEnabled(false);
        nextButton.setAlpha(0.5f);

        // Start the 20-second timer
        startTimer();

        // Listener for the submit button
        submitButton.setOnClickListener(v -> {
            if (!answered) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(PostAsses15.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cancel the timer
                countDownTimer.cancel();

                // Reset the button tint for all radio buttons
                resetRadioButtonColors();

                // Define custom darker colors
                int darkGreen = 0xFF00CC00;
                int darkRed = 0xFFCC0000;

                if (selectedId == correctAnswerId) {
                    // Correct answer: highlight the selected radio button circle with dark green
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PostAsses15.this, "Correct!", Toast.LENGTH_SHORT).show();
                    score++;  // Increase score
                } else {
                    // Wrong answer: highlight the selected radio button circle with dark red
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    selectedRadioButton.setButtonTintList(ColorStateList.valueOf(darkRed));

                    // Also highlight the correct answer in dark green
                    RadioButton correctRadioButton = findViewById(correctAnswerId);
                    correctRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
                    Toast.makeText(PostAsses15.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                }

                answered = true;
                disableRadioGroup();
                submitButton.setEnabled(false);
                submitButton.setAlpha(0.5f);
                nextButton.setEnabled(true);
                nextButton.setAlpha(1.0f);
            }
        });

        // Listener for the next button to redirect to PostAssess1After.class
        nextButton.setOnClickListener(v -> {
            if (answered) {
                Intent intent = new Intent(PostAsses15.this, PostAssess1After.class);
                intent.putExtra("score", score);
                startActivity(intent);
            } else {
                Toast.makeText(PostAsses15.this, "Please submit your answer first", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PostAsses15.this, "Time is up! Correct answer is shown.", Toast.LENGTH_SHORT).show();

                int darkGreen = 0xFF00CC00;
                RadioButton correctRadioButton = findViewById(correctAnswerId);
                correctRadioButton.setButtonTintList(ColorStateList.valueOf(darkGreen));
            }
        }.start();
    }

    /**
     * Disables all RadioButtons in the RadioGroup.
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