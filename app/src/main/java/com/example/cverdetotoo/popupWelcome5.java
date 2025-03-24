package com.example.cverdetotoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class popupWelcome5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_welcome5);  // replace with your layout XML

        // Find the button by its ID
        Button btnWNext1 = findViewById(R.id.btnWNext5);
        Button btnWBack1 = findViewById(R.id.btnWBack5);

        // Set an onClickListener for the button
        btnWBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to popupWelcome activity
                Intent intent = new Intent(popupWelcome5.this, popupWelcome4.class);

                // Start the new activity (popupWelcome)
                startActivity(intent);

                // Optionally, you can finish this activity if you don't want to keep it in the back stack
                finish();
            }
        });

        btnWNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to popupWelcome activity
                Intent intent = new Intent(popupWelcome5.this, navbar .class);

                // Start the new activity (popupWelcome)
                startActivity(intent);

                // Optionally, you can finish this activity if you don't want to keep it in the back stack
                finish();
            }
        });
    }
}

