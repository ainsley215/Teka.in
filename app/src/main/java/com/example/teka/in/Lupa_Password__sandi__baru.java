package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Lupa_Password__sandi__baru extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private View savePasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password_sandi_baru_);

        newPasswordEditText = findViewById(R.id.sandi_baru_txt);
        confirmPasswordEditText = findViewById(R.id.kofir_sandi_txt);
        savePasswordButton = findViewById(R.id.Button_Send);
        auth = FirebaseAuth.getInstance();

        savePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Kata sandi tidak cocok!", Toast.LENGTH_SHORT).show();
            } else {
                updatePassword(newPassword);
            }
        });
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Lupa_Password__sandi__baru.this, "Kata sandi berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Lupa_Password__sandi__baru.this, Login.class));
                    finish();
                } else {
                    Toast.makeText(Lupa_Password__sandi__baru.this, "Gagal memperbarui kata sandi!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}