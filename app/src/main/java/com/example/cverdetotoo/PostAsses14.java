package com.example.cverdetotoo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.ColorStateList;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PostAsses14 extends AppCompatActivity {

    private int score = 0;  // Score tracking
    private static final long TOTAL_TIME = 20000; // 20 seconds countdown
    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private int correctAnswerId = R.id.prebtn1b; // Correct answer
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
        setContentView(R.layout.activity_post_asses14);

        // Retrieve score from previous activity
        score = getIntent().getIntExtra("score", 0);

        // Initialize UI components
        timerTextView = findViewById(R.id.posttimer14);
        radioGroup = findViewById(R.id.radiopostQ14);
        submitButton = findViewById(R.id.postq14);
        nextButton = findViewById(R.id.postnext14);

        // Initially disable "Next" button
        updateButtonState(nextButton, false);

        // Start the countdown timer
        startTimer();

        // Set up event listeners
        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> goToNextQuestion());
    }

    /**
     * Starts the countdown timer.
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time left: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Time's up!");
                handleTimeUp();
            }
        }.start();
    }

    /**
     * Handles answer selection and scoring.
     */
    private void checkAnswer() {
        if (answered) return;  // Prevent multiple submissions

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        // Stop timer
        countDownTimer.cancel();
        answered = true;

        // Reset colors before updating selection
        resetRadioButtonColors();

        // Define feedback colors
        int darkGreen = 0xFF00CC00;
        int darkRed = 0xFFCC0000;

        // Check if the selected answer is correct
        if (selectedId == correctAnswerId) {
            highlightRadioButton(selectedId, darkGreen);
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            highlightRadioButton(selectedId, darkRed);
            highlightRadioButton(correctAnswerId, darkGreen);
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }

        disableRadioGroup();
        updateButtonState(submitButton, false);
        updateButtonState(nextButton, true);
    }

    /**
     * Handles scenario when time runs out.
     */
    private void handleTimeUp() {
        answered = true;
        disableRadioGroup();
        radioGroup.check(correctAnswerId);
        highlightRadioButton(correctAnswerId, 0xFF00CC00);  // Dark green for correct answer
        updateButtonState(submitButton, false);
        updateButtonState(nextButton, true);
        Toast.makeText(this, "Time is up! Correct answer is shown.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to the next question.
     */
    private void goToNextQuestion() {
        if (!answered) {
            Toast.makeText(this, "Please submit your answer first", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PostAsses15.class);
        intent.putExtra("score", score);
        startActivity(intent);
    }

    /**
     * Disables all options in the RadioGroup.
     */
    private void disableRadioGroup() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    /**
     * Resets the text color and button tint of all RadioButtons.
     */
    private void resetRadioButtonColors() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            if (radioGroup.getChildAt(i) instanceof RadioButton) {
                RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
                rb.setTextColor(Color.BLACK);
                rb.setButtonTintList(null);
            }
        }
    }

    /**
     * Highlights a selected RadioButton with a specific color.
     */
    private void highlightRadioButton(int radioButtonId, int color) {
        RadioButton rb = findViewById(radioButtonId);
        rb.setTextColor(color);
        rb.setButtonTintList(ColorStateList.valueOf(color));
    }

    /**
     * Updates button state (enabled/disabled) and opacity.
     */
    private void updateButtonState(Button button, boolean isEnabled) {
        button.setEnabled(isEnabled);
        button.setAlpha(isEnabled ? 1.0f : 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}