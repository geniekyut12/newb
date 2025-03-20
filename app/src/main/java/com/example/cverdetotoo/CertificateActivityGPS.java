package com.example.cverdetotoo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CertificateActivityGPS extends AppCompatActivity {

    private static final String TAG = "CertificateActivity";
    private ImageView certificateView;
    private Bitmap certificateTemplate;
    private String fullName;
    private Bitmap generatedCertificate;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        certificateView = findViewById(R.id.certificateView);
        Button btnDownloadCertificate = findViewById(R.id.btnDownloadCertificate);
        Button btnSavePDF = findViewById(R.id.btnDownloadPDF); // Optional PDF button

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        certificateTemplate = BitmapFactory.decodeResource(getResources(), R.drawable.cert1);

        String usernameKey = auth.getCurrentUser().getDisplayName();
        if (usernameKey == null || usernameKey.isEmpty()) {
            fullName = "Firstname Lastname";
            generateCertificate(fullName);
        } else {
            db.collection("users").document(usernameKey).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            if (firstName == null) firstName = "";
                            if (lastName == null) lastName = "";
                            fullName = (firstName + " " + lastName).trim();
                            if (fullName.isEmpty()) fullName = "Firstname Lastname";
                        } else {
                            fullName = "Firstname Lastname";
                        }
                        generateCertificate(fullName);
                    })
                    .addOnFailureListener(e -> {
                        fullName = "Firstname Lastname";
                        generateCertificate(fullName);
                    });
        }

        btnDownloadCertificate.setOnClickListener(v -> {
            if (generatedCertificate != null) {
                // Save the certificate image to gallery
                saveCertificateToGallery(generatedCertificate);
                // Save certificate details to Firestore using username as document id
                saveCertificateDataToFirestore();
                // After saving, redirect to the Navbar activity
                redirectToNavbar();
            } else {
                Toast.makeText(CertificateActivityGPS.this, "Certificate not generated", Toast.LENGTH_SHORT).show();
            }
        });

        btnSavePDF.setOnClickListener(v -> {
            if (generatedCertificate != null) {
                // Save certificate as PDF
                saveCertificateToPDF(generatedCertificate);
                // Save certificate details to Firestore using username as document id
                saveCertificateDataToFirestore();
                // After saving, redirect to the Navbar activity
                redirectToNavbar();
            } else {
                Toast.makeText(CertificateActivityGPS.this, "Certificate not generated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateCertificate(String name) {
        // Create a mutable copy of the certificate template
        Bitmap mutableBitmap = certificateTemplate.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        float centerX = mutableBitmap.getWidth() / 2f;
        float centerY = mutableBitmap.getHeight() / 2f;

        // Set up paint for the name text
        Paint namePaint = new Paint();
        namePaint.setColor(Color.BLACK);
        namePaint.setTextSize(120);  // Adjust based on your template
        namePaint.setFakeBoldText(true);
        namePaint.setTextAlign(Paint.Align.CENTER);

        // Measure name text height properly for alignment
        Paint.FontMetrics nameMetrics = namePaint.getFontMetrics();
        float nameHeight = nameMetrics.descent - nameMetrics.ascent;
        float nameY = centerY - (nameHeight / -25) - 12;  // Adjust for exact placement

        // Draw the name in the exact middle
        canvas.drawText(name, centerX, nameY, namePaint);

        // Set up paint for the date text
        Paint datePaint = new Paint();
        datePaint.setColor(Color.BLACK);
        datePaint.setTextSize(80);  // Adjust as needed
        datePaint.setTextAlign(Paint.Align.CENTER);

        // Get the formatted date
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Measure date text height
        Paint.FontMetrics dateMetrics = datePaint.getFontMetrics();
        float dateHeight = dateMetrics.descent - dateMetrics.ascent;
        float dateY = nameY + 750;  // Adjust to match the "DATE" line on your certificate

        // Draw the date correctly aligned
        canvas.drawText(currentDate, centerX, dateY, datePaint);

        generatedCertificate = mutableBitmap;
        certificateView.setImageBitmap(generatedCertificate);
    }

    private void saveCertificateToGallery(Bitmap bitmap) {
        OutputStream fos;
        try {
            String fileName = "certificate_" + System.currentTimeMillis() + ".png";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyCertificates");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MyCertificates";
                File file = new File(imagesDir);
                if (!file.exists()) file.mkdirs();
                File image = new File(file, fileName);
                fos = new FileOutputStream(image);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Certificate saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving certificate", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCertificateToPDF(Bitmap bitmap) {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, 0, 0, paint);
            pdfDocument.finishPage(page);

            String fileName = "certificate_" + System.currentTimeMillis() + ".pdf";

            File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyCertificates");
            if (!pdfDir.exists()) pdfDir.mkdirs();

            File pdfFile = new File(pdfDir, fileName);
            OutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(this, "Certificate saved as PDF in Documents/MyCertificates/", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves certificate details (name and date) to Firestore in the "certificate" collection.
     * The document ID is based on the user's username (or UID as fallback).
     */
    private void saveCertificateDataToFirestore() {
        // Get the current date formatted as desired
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Retrieve the current user's username; if not available, use UID
        String username = auth.getCurrentUser().getDisplayName();
        if (username == null || username.isEmpty()) {
            username = auth.getCurrentUser().getUid();
        }
        // Replace spaces with underscores for a valid document ID
        String documentId = username.replaceAll("\\s+", "_");

        // Prepare the data to be saved
        Map<String, Object> certificateData = new HashMap<>();
        certificateData.put("name", fullName);
        certificateData.put("date", currentDate);
        certificateData.put("username", username);
        // Add additional fields if needed

        // Save to Firestore in the "certificate" collection using the username as the document ID
        db.collection("certificate").document(documentId)
                .set(certificateData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Certificate data saved to Firestore", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving certificate data", Toast.LENGTH_SHORT).show()
                );
    }

    /**
     * Redirects the user to the Navbar activity.
     */
    private void redirectToNavbar() {
        Intent intent = new Intent(CertificateActivityGPS.this, navbar.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        // Do nothing so that the back button does not navigate to the previous activity.
    }
}
