package com.example.teka.in;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Lupa_Password__email extends AppCompatActivity {

    private EditText emailEditText;
    private View sendOtpButton;
    private String emailUsername;
    private String emailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password__email_);

        emailEditText = findViewById(R.id.masukan_email);
        sendOtpButton = findViewById(R.id.Button_Send);

        // Load email credentials from properties file
        loadEmailCredentials();

        sendOtpButton.setOnClickListener(view -> {
            String userEmail = emailEditText.getText().toString().trim();
            if (!userEmail.isEmpty()) {
                int otp = 100000 + new Random().nextInt(900000); // Generate OTP between 100000 and 999999
                kirimEmail(userEmail, otp);
                Toast.makeText(Lupa_Password__email.this, "OTP telah dikirim ke email Anda.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Lupa_Password__email.this, "Mohon masukkan alamat email.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEmailCredentials() {
        Properties props = new Properties();
        try (InputStream is = getAssets().open("config.properties")) {
            props.load(is);
            emailUsername = props.getProperty("email.username");
            emailPassword = props.getProperty("email.password");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load email credentials.", Toast.LENGTH_SHORT).show();
        }
    }

    private void kirimEmail(final String userEmail, final int otp) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailUsername));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
                message.setSubject("Your OTP for Password Reset");
                message.setText("Your OTP is: " + otp);
                Transport.send(message);
                Log.d("Email", "OTP email sent successfully.");
            } catch (MessagingException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Lupa_Password__email.this, "Failed to send OTP email.", Toast.LENGTH_SHORT).show());
            }
        }).start();

        // Save OTP in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("otp", otp);
        otpData.put("email", userEmail);

        db.collection("otpCollection")
                .document(userEmail)
                .set(otpData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "OTP document added successfully."))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding document", e));
    }
}

/*package com.example.teka.in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Lupa_Password__email extends AppCompatActivity {

    private EditText emailEditText;
    private View sendOtpButton; // Mengganti View dengan Button
    private String emailUsername;
    private String emailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password__email_);

        emailEditText = findViewById(R.id.masukan_email);
        sendOtpButton = findViewById(R.id.Button_Send);

        // Load email credentials from properties file
        loadEmailCredentials();

        sendOtpButton.setOnClickListener(view -> {
            String userEmail = emailEditText.getText().toString().trim();
            if (!userEmail.isEmpty() && isValidEmail(userEmail)) {
                int otp = 100000 + new Random().nextInt(900000); // Generate OTP between 100000 and 999999
                kirimEmail(userEmail, otp);
                Toast.makeText(Lupa_Password__email.this, "OTP telah dikirim ke email Anda.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Lupa_Password__email.this, Lupa_Password__kode.class);
                intent.putExtra("email", userEmail); // Pass email if needed
                startActivity(intent);
            } else {
                Toast.makeText(Lupa_Password__email.this, "Mohon masukkan alamat email yang valid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEmailCredentials() {
        Properties props = new Properties();
        try (InputStream is = getAssets().open("config.properties")) {
            props.load(is);
            emailUsername = props.getProperty("email.username");
            emailPassword = props.getProperty("email.password");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat kredensial email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void kirimEmail(final String userEmail, final int otp) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailUsername));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
                message.setSubject("Your OTP for Password Reset");
                message.setText("Your OTP is: " + otp);
                Transport.send(message);
                Log.d("Email", "Email OTP berhasil dikirim.");
            } catch (MessagingException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Lupa_Password__email.this, "Gagal mengirim email OTP.", Toast.LENGTH_SHORT).show());
            }
        }).start();

        // Save OTP in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("otp", otp);
        otpData.put("email", userEmail);

        db.collection("otpCollection")
                .document(userEmail)
                .set(otpData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Dokumen OTP berhasil ditambahkan."))
                .addOnFailureListener(e -> Log.e("Firestore", "Error menambahkan dokumen", e));
    }

    private boolean isValidEmail(String email) {
        // Regex untuk memvalidasi email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.matches(emailPattern, email);
    }
}*/
