package com.example.cverdetotoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GPSstart extends AppCompatActivity {


    private int currentFactIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure activity_battle_eco_start.xml is in res/layout
        setContentView(R.layout.activity_gpsstart);

        // Find views by ID
        View parentLayout = findViewById(R.id.parentLayout12);
        final TextView tvFact = findViewById(R.id.tvFact12);

        // (Optional) Show the first fact immediately
        // tvFact.setText(facts[currentFactIndex]);

        // Set an OnClickListener on the root layout
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Optionally show a Toast message
                Toast.makeText(GPSstart.this, "Screen tapped!", Toast.LENGTH_SHORT).show();

                // Navigate to NextActivity when the screen is tapped
                Intent intent = new Intent(GPSstart.this, GPS.class);
                startActivity(intent);
            }
        });
    }
}
