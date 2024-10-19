package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.text.InputType;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.List;

public class Daftar__wali__murid extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private View rectangle_287;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.daftar__wali__murid_);

        rectangle_287 = findViewById(R.id.rectangle_287);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        View daftarButton = findViewById(R.id._rectangle_349);
        View masukGoogleButton = findViewById(R.id.masuk_text);
        EditText passwordEditText = findViewById(R.id.masukan_password);
        EditText konfirmasiPasswordEditText = findViewById(R.id.ulangi_password_anda);

        // Set up Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // OnClickListener for Daftar button
        daftarButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            String confirmPassword = konfirmasiPasswordEditText.getText().toString();

            // Memeriksa apakah password dan konfirmasi password cocok
            if (!password.equals(confirmPassword)) {
                Toast.makeText(Daftar__wali__murid.this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                return; // Keluar dari fungsi jika password tidak cocok
            }

            // Validasi password
            List<String> validationErrors = validatePassword(password);
            if (!validationErrors.isEmpty()) {
                // Tampilkan kesalahan
                for (String error : validationErrors) {
                    Toast.makeText(Daftar__wali__murid.this, error, Toast.LENGTH_SHORT).show();
                }
                return; // Keluar dari fungsi jika ada kesalahan
            }

            // Lanjutkan dengan proses pendaftaran
            Intent intent = new Intent(Daftar__wali__murid.this, Login.class);
            startActivity(intent);
            finish();
        });

        // OnClickListener for Google Sign-In button
        rectangle_287.setOnClickListener(v -> signIn());



        // Password visibility toggle for the password EditText
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility(passwordEditText);
                }
            }
            return false;
        });

        // Password visibility toggle for the confirmation EditText
        konfirmasiPasswordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (konfirmasiPasswordEditText.getRight() - konfirmasiPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility(konfirmasiPasswordEditText);
                }
            }
            return false;
        });
    }

    private List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        // Cek panjang password
        if (password.length() < 8) {
            errors.add("Password harus terdiri dari setidaknya 8 karakter.");
        }

        // Cek apakah password mengandung huruf besar, huruf kecil, angka, dan karakter khusus
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

        return errors; // Kembalikan daftar kesalahan
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
                    String idToken = account.getIdToken(); // Get ID Token from Google account
                    firebaseAuth(idToken); // Authenticate with Firebase
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Daftar__wali__murid.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Daftar__wali__murid.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void togglePasswordVisibility(EditText passwordEditText) {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
            isPasswordVisible = false;
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
            isPasswordVisible = true;
        }
        // Move the cursor to the end of the input
        passwordEditText.setSelection(passwordEditText.length());
    }
}