package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Lupa_Password__kode extends AppCompatActivity {

    private EditText otpEditText;
    private View verifyOtpButton;
    private String userEmail; // Menyimpan email pengguna

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lupa_password__kode_);

        otpEditText = findViewById(R.id.masukan_kode); // Ganti dengan ID yang sesuai
        verifyOtpButton = findViewById(R.id.Button_Send); // Ganti dengan ID yang sesuai

        // Ambil email dari Intent
        userEmail = getIntent().getStringExtra("userEmail");

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOtp();
            }
        });
    }

    private void verifyOtp() {
        String userInputOtp = otpEditText.getText().toString().trim();
        if (!userInputOtp.isEmpty()) {
            int userOtp = Integer.parseInt(userInputOtp);

            // Ambil OTP dari Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("otpCollection").document(userEmail) // Menggunakan email pengguna
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                int savedOtp = document.getLong("otp").intValue(); // Mengambil OTP yang disimpan

                                if (userOtp == savedOtp) {
                                    Toast.makeText(Lupa_Password__kode.this, "OTP valid. Silakan lanjut ke pengaturan kata sandi baru.", Toast.LENGTH_SHORT).show();
                                    // Panggil metode untuk reset kata sandi
                                    Intent intent = new Intent(Lupa_Password__kode.this, Lupa_Password__sandi__baru.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Lupa_Password__kode.this, "OTP tidak valid. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Lupa_Password__kode.this, "Tidak ada OTP yang ditemukan. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Lupa_Password__kode.this, "Gagal mengambil OTP. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Mohon masukkan OTP.", Toast.LENGTH_SHORT).show();
        }
    }
}