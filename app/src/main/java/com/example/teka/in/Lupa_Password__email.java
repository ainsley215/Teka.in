package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
//import com.google.firebase.auth.FirebaseTooManyRequestsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Lupa_Password__email extends AppCompatActivity {

    private EditText phoneEditText;
    private View sendOtpButton;
    private FirebaseAuth auth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password__email_);

        phoneEditText = findViewById(R.id.masukan_email);
        sendOtpButton = findViewById(R.id.Button_Send);
        auth = FirebaseAuth.getInstance();

        sendOtpButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            if (!phone.isEmpty()) {
                sendOtp(phone);
            } else {
                Toast.makeText(this, "Nomor telepon harus diisi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtp(String phoneNumber) {
        // Set up PhoneAuthOptions to configure OTP sending
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callbacks for phone number verification
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // Jika verifikasi otomatis berhasil (untuk beberapa perangkat)
            Toast.makeText(Lupa_Password__email.this, "Verifikasi berhasil otomatis!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // Jika verifikasi gagal
            Toast.makeText(Lupa_Password__email.this, "Gagal mengirim OTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // OTP berhasil dikirim, simpan verificationId dan pindah ke halaman Masukan OTP
            Lupa_Password__email.this.verificationId = verificationId;
            Toast.makeText(Lupa_Password__email.this, "OTP terkirim!", Toast.LENGTH_SHORT).show();

            // Pindah ke halaman Masukan OTP
            Intent intent = new Intent(Lupa_Password__email.this, Lupa_Password__kode.class);
            intent.putExtra("verificationId", verificationId); // Kirim verificationId ke activity berikutnya
            startActivity(intent);
        }
    };
}
