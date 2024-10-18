package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Awal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.awal_);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.awal_), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View daftarButton = findViewById(R.id.daftar_);
        View masukButton = findViewById(R.id.masuk_);

        daftarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Awal.this, Daftar__wali__murid.class);
            startActivity(intent);
            finish();
        });

        masukButton.setOnClickListener(v -> {
            Intent intent = new Intent(Awal.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}