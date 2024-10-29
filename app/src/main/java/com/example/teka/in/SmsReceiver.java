package com.example.teka.in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String messageBody = smsMessage.getMessageBody();

                // Cari OTP dalam pesan
                String otp = extractOtp(messageBody);
                if (otp != null) {
                    // Kirim OTP ke aktivitas
                    Intent otpIntent = new Intent(context, Lupa_Password__kode.class);
                    otpIntent.putExtra("otp", otp);
                    context.startActivity(otpIntent);
                }
            }
        }
    }

    private String extractOtp(String message) {
        // Misalnya, jika format SMS adalah "Your OTP is: 123456"
        String otpPattern = "\\d{6}"; // Menyaring angka 6 digit
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(otpPattern);
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}