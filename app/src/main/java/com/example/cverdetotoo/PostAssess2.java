package com.example.cverdetotoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PostAssess2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the action bar (header)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_post_assess2);

        // Find the button by its ID
        Button startButton = findViewById(R.id.postbtnstart2);

        // Set an OnClickListener on the button to start PreAsses11 activity
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostAssess2.this, PostAsses21.class);
                startActivity(intent);
            }
        });
    }
}
