package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Dashboard extends AppCompatActivity {

    private TextView notificationBadge;
    private ImageView notificationIcon; // Deklarasi ImageView untuk ikon notifikasi
    private ImageView profileImage; // Deklarasi ImageView untuk gambar profil
    private int notificationCount = 5;
    private String photoUrl = "https://example.com/path/to/profile/image.jpg"; // Ganti dengan URL gambar yang sesuai

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Pastikan ini diperlukan
        setContentView(R.layout.dashboard_);

        notificationBadge = findViewById(R.id.notification_badge);
        notificationIcon = findViewById(R.id.notification_icon); // Inisialisasi ImageView
        profileImage = findViewById(R.id.profile_image); // Inisialisasi ImageView untuk gambar profil

        updateNotificationBadge(notificationCount);
        loadProfileImage(); // Memuat gambar profil

        // Set OnClickListener untuk ikon notifikasi
        notificationIcon.setOnClickListener(v -> {
            // Tindakan saat ikon notifikasi diklik
            Intent intent = new Intent(Dashboard.this, Notification.class);
            startActivity(intent);
        });
    }

    private void updateNotificationBadge(int count) {
        if (count > 0) {
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisibility(View.VISIBLE);
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    private void loadProfileImage() {
        // Menggunakan Glide untuk memuat gambar ke dalam ImageView
        Glide.with(this) // Menunjukkan konteks
                .load(photoUrl) // URL gambar
                .placeholder(R.drawable.ic_profile_placeholder) // Gambar placeholder
                .into(profileImage); // ImageView tempat gambar akan ditampilkan
    }
}