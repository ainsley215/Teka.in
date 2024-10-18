package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_);

        // Menambahkan OnClickListener pada elemen __bg__splash
        findViewById(R.id.__bg__splash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Beralih ke LoginActivity
                Intent intent = new Intent(Splash.this, Awal.class);
                startActivity(intent);
                finish(); // Menutup Splash agar tidak bisa kembali ke halaman ini
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}