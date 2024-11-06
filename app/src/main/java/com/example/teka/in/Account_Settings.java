package com.example.teka.in;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Account_Settings extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    private Button saveButton;
    private Button changeProfilePictureButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings_);

        profileImageView = findViewById(R.id.profile_image);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        addressEditText = findViewById(R.id.address);
        saveButton = findViewById(R.id.save_button);
        changeProfilePictureButton = findViewById(R.id.change_profile_picture);

        // Ambil data pengguna dari Firestore
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser ().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        String address = documentSnapshot.getString("address");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        usernameEditText.setText(username != null ? username : "");
                        emailEditText.setText(email);
                        addressEditText.setText(address != null ? address : "");

                        // Set gambar profil jika ada
                        if (profileImageUrl != null) {
                            // Anda bisa menggunakan library seperti Glide atau Picasso untuk memuat gambar
                            // Glide.with(this).load(profileImageUrl).into(profileImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                });

        // Set listener untuk tombol Save
        saveButton.setOnClickListener(v -> saveUserData());

        // Set listener untuk tombol ganti foto profil
        changeProfilePictureButton.setOnClickListener(v -> openGallery());
    }

    private void saveUserData() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Validasi input
        if (username.isEmpty() || email.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan data ke Firestore
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser ().getUid();

        // Jika ada gambar yang dipilih, upload ke Firebase Storage
        if (imageUri != null) {
            uploadImageToFirebase(imageUri, userId, username, email, address);
        } else {
            // Jika tidak ada gambar, simpan data tanpa gambar
            db.collection("users").document(userId)
                    .update("username", username, "email", email, "address", address)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "User  data updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String userId, String username, String email, String address) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + userId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Simpan URL gambar di Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(userId)
                                    .update("username", username, "email", email, "address", address, "profileImageUrl", uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "User  data updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                                    });
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}