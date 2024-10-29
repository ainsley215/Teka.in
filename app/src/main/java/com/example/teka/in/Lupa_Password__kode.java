package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class Lupa_Password__kode extends AppCompatActivity {

    private EditText otpEditText;
    private View verifyOtpButton;
    private FirebaseAuth auth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lupa_password__kode_);

        otpEditText = findViewById(R.id.masukan_kode);
        verifyOtpButton = findViewById(R.id.Button_Send);
        auth = FirebaseAuth.getInstance();

        // Ambil verificationId dari Intent
        verificationId = getIntent().getStringExtra("verificationId");

        // Ambil OTP dari Intent jika ada
        String otp = getIntent().getStringExtra("otp");
        if (otp != null) {
            otpEditText.setText(otp); // Isi otomatis OTP
        }

        verifyOtpButton.setOnClickListener(v -> {
            String otpInput = otpEditText.getText().toString().trim();
            if (!otpInput.isEmpty() && verificationId != null) {
                verifyOtp(verificationId, otpInput);
            } else {
                Toast.makeText(this, "Masukkan OTP yang valid!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtp(String verificationId, String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Lupa_Password__kode.this, "Verifikasi berhasil!", Toast.LENGTH_SHORT).show();
                // Pindah ke halaman untuk mengatur kata sandi baru
                startActivity(new Intent(Lupa_Password__kode.this, Lupa_Password__sandi__baru.class));
                finish();
            } else {
                Toast.makeText(Lupa_Password__kode.this, "Verifikasi gagal! Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}