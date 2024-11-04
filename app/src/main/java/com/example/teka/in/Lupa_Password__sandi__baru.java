package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Lupa_Password__sandi__baru extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private View savePasswordButton;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password_sandi_baru_);

        newPasswordEditText = findViewById(R.id.sandi_baru_txt);
        confirmPasswordEditText = findViewById(R.id.kofir_sandi_txt);
        savePasswordButton = findViewById(R.id.Button_Send);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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

                    // Simpan status pembaruan kata sandi ke Firestore
                    Map<String, Object> updateStatus = new HashMap<>();
                    updateStatus.put("passwordUpdated", true);
                    updateStatus.put("updatedAt", System.currentTimeMillis());

                    firestore.collection("users").document(user.getUid())
                            .update(updateStatus)
                            .addOnSuccessListener(aVoid -> {
                                // Jika penyimpanan berhasil
                                startActivity(new Intent(Lupa_Password__sandi__baru.this, Login.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Lupa_Password__sandi__baru.this, "Gagal menyimpan status ke Firestore.", Toast.LENGTH_SHORT).show();
                            });

                } else {
                    Toast.makeText(Lupa_Password__sandi__baru.this, "Gagal memperbarui kata sandi!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}