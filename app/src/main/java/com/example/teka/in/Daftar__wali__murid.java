package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Daftar__wali__murid extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar__wali__murid_);

        // Firebase and Google Sign-In setup
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI Components
        EditText nameEditText = findViewById(R.id.masukan_nama);
        EditText addressEditText = findViewById(R.id.masukan_alamat);
        EditText emailEditText = findViewById(R.id.masukan_alamat_email);
        EditText passwordEditText = findViewById(R.id.masukan_password);
        EditText confirmPasswordEditText = findViewById(R.id.ulangi_password_anda);

        // Daftar button handling
        findViewById(R.id._rectangle_349).setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Check if password matches
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate password
            List<String> validationErrors = validatePassword(password);
            if (!validationErrors.isEmpty()) {
                for (String error : validationErrors) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // Proceed with user registration
            registerUser(name, address, email, password);
        });

        TextView MenuDaftar = findViewById(R.id.masuk_text); // Pastikan ID benar di layout XML
        MenuDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(Daftar__wali__murid.this, Login.class);
            startActivity(intent);
        });

        // Google Sign-In handling
        findViewById(R.id.group_google).setOnClickListener(v -> signIn());

        // Password visibility toggle
        setupPasswordVisibilityToggle(passwordEditText, true);
        setupPasswordVisibilityToggle(confirmPasswordEditText, false);
    }

    private List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        if (password.length() < 8) {
            errors.add("Password harus terdiri dari setidaknya 8 karakter.");
        }
        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password harus mengandung setidaknya satu huruf besar.");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.add("Password harus mengandung setidaknya satu huruf kecil.");
        }
        if (!password.matches(".*\\d.*")) {
            errors.add("Password harus mengandung setidaknya satu angka.");
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            errors.add("Password harus mengandung setidaknya satu karakter khusus.");
        }
        return errors;
    }

    private void registerUser(String name, String address, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getCurrentUser().getUid();
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("address", address);
                user.put("email", email);

                firestore.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, Login.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupPasswordVisibilityToggle(EditText passwordEditText, boolean isPrimaryPassword) {
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility(passwordEditText, isPrimaryPassword);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText passwordEditText, boolean isPrimaryPassword) {
        if (isPrimaryPassword) {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                isPasswordVisible = true;
            }
        } else {
            if (isConfirmPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                isConfirmPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                isConfirmPasswordVisible = true;
            }
        }
        // Move cursor to the end
        passwordEditText.setSelection(passwordEditText.length());
    }
}