package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

public class Dashboard extends AppCompatActivity {

    private TextView notificationBadge;
    private ImageView notificationIcon; // Deklarasi ImageView untuk ikon notifikasi
    private ImageView profileImage; // Deklarasi ImageView untuk gambar profil
    private int notificationCount = 5;
    private String photoUrl = "https://example.com/path/to/profile/image.jpg"; // Ganti dengan URL gambar yang sesuai
    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private int[] images = {
            R.drawable.image1, // Ganti dengan nama gambar Anda
            R.drawable.image2,
            R.drawable.image3
            /*R.drawable.image4*/
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Pastikan ini diperlukan
        setContentView(R.layout.dashboard_);

        notificationBadge = findViewById(R.id.notification_badge);
        notificationIcon = findViewById(R.id.notification_icon); // Inisialisasi ImageView
        profileImage = findViewById(R.id.profile_image); // Inisialisasi ImageView untuk gambar profil
        viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this, images);
        viewPager.setAdapter(adapter);

        updateNotificationBadge(notificationCount);
        loadProfileImage(); // Memuat gambar profil

        // Set OnClickListener untuk ikon notifikasi
        notificationIcon.setOnClickListener(v -> {
            // Tindakan saat ikon notifikasi diklik
            Intent intent = new Intent(Dashboard.this, Notification.class);
            startActivity(intent);
        });

        // Auto scroll
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                if (currentPage == images.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Ganti gambar setiap 3 detik
            }
        };
        handler.postDelayed(runnable, 3000); // Mulai auto scroll setelah 3 detik
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Hentikan auto scroll saat aktivitas tidak terlihat
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000); // Mulai kembali auto scroll saat aktivitas terlihat
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
        Glide.with(this) // Menunjukkan konteks (seperti restoran)
                .load(photoUrl) // URL gambar (seperti menu)
                .placeholder(R.drawable.ic_profile_placeholder) // Gambar placeholder (makanan sementara)
                .into(profileImage); // ImageView tempat gambar akan ditampilkan (meja Anda)
    }
}