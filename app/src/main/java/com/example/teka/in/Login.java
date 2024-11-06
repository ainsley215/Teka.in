package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private View googleSignInButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private View loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_);  // Pastikan nama file layout benar

        EditText passwordEditText = findViewById(R.id.masukkan_password); // Pastikan ID benar di layout XML
        googleSignInButton = findViewById(R.id.group_google); // Pastikan ID benar di layout XML
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.masukan_AlamatEmail);
        loginButton = findViewById(R.id.LoginButton);

        loginButton.setOnClickListener(v -> loginUser ());

        // Inisialisasi Google Sign-In client
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id)) // Isi dengan client ID dari google-services.json
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();

        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

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

        TextView forgotPasswordButton = findViewById(R.id.lupa_password); // Pastikan ID benar di layout XML
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Lupa_Password__email.class);
            startActivity(intent);
        });

        TextView MenuDaftar = findViewById(R.id.daftar_text); // Pastikan ID benar di layout XML
        MenuDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Daftar__wali__murid.class);
            startActivity(intent);
        });
    }

    private void loginUser () {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser  user = mAuth.getCurrentUser ();
                        checkUserInFirestore(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInFirestore(FirebaseUser  user) {
        if (user != null) {
            firestore.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // User exists in Firestore
                            Boolean isFirstTime = documentSnapshot.getBoolean("isFirstTimeLogin");
                            if (isFirstTime != null && isFirstTime) {
                                // If it's the first time logging in, redirect to account settings
                                Intent intent = new Intent(Login.this, Dashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If it's not the first time, redirect to the dashboard
                                Intent intent = new Intent(Login.this, Account_Settings.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(Login.this, "User  data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Login.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                        Log.w("Firestore", "Error getting document", e);
                    });
        } else {
            Toast.makeText(Login.this, "User  is null.", Toast.LENGTH_SHORT).show();
        }
    }




    private void signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e("GoogleSignIn", "Error starting sign-in", e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(Login.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    Log.e("GoogleSignIn", "Google Sign-In failed", e);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(Login.this, "Sign In Success", Toast.LENGTH_SHORT).show();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(Login.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "SignInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            checkIfFirstTimeLogin(user);
        }
    }

    private void checkIfFirstTimeLogin(FirebaseUser user) {
        if (user != null) {
            firestore.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Boolean isFirstTime = documentSnapshot.getBoolean("isFirstTimeLogin");
                            if (isFirstTime != null && isFirstTime) {
                                firestore.collection("users").document(user.getUid())
                                        .update("isFirstTimeLogin", false)
                                        .addOnSuccessListener(aVoid -> {
                                            Intent intent = new Intent(Login.this, Dashboard.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error updating user data.", Toast.LENGTH_SHORT).show();
                                            Log.w("Firestore", "Error updating document", e);
                                        });
                            } else {
                                Intent intent = new Intent(Login.this, Account_Settings.class); //
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                        Log.w("Firestore", "Error getting document", e);
                    });
        }
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