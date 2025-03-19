package com.example.cverdetotoo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class video1 extends AppCompatActivity {

    private VideoView storyVideo;
    private ImageButton closeButton;
    private ImageButton nextButton; // Changed from Button to ImageButton
    private ProgressBar progressBar;
    private Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video1);

        // Initialize UI elements
        storyVideo = findViewById(R.id.story_video);
        closeButton = findViewById(R.id.close_button);
        progressBar = findViewById(R.id.progress_bar);
        nextButton = findViewById(R.id.next_button); // This ImageButton should be in your XML layout

        // Load the video from the raw folder
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid1intro);
        storyVideo.setVideoURI(videoUri);

        // Listener for when the video is ready to play (preserving validations)
        storyVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // Validate video duration is available
                if (storyVideo.getDuration() > 0) {
                    progressBar.setMax(storyVideo.getDuration());
                }
                storyVideo.start();
                updateProgressBar();
            }
        });

        // Redirect to PostAssess1 when the video completes
        storyVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(video1.this, PostAssess1.class);
                startActivity(intent);
                finish(); // Optional: finish the current activity
            }
        });

        // Close button action (existing validation remains intact)
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Next ImageButton action to manually redirect to PostAssess1
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(video1.this, PostAssess1.class);
                startActivity(intent);
                finish(); // Optional: finish current activity if desired
            }
        });
    }

    // Runnable to update the progress bar every 100ms
    private void updateProgressBar() {
        progressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (storyVideo != null && storyVideo.isPlaying()) {
                    progressBar.setProgress(storyVideo.getCurrentPosition());
                    progressHandler.postDelayed(this, 100);
                }
            }
        }, 100);
    }
}
