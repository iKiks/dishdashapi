package com.kiks.dishdashapi.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration OTP_TTL = Duration.ofMinutes(1);

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    public String generateAndStoreOtp(String key) {
        String otp = generate6DigitOtp();
        Instant expiresAt = Instant.now().plus(OTP_TTL);
        otpStore.put(key, new OtpData(otp, expiresAt));
        return otp;
    }

    public boolean verifyOtp(String key, String providedOtp) {
        OtpData data = otpStore.get(key);
        if (data == null) return false;

        // expired -> delete + fail
        if (Instant.now().isAfter(data.expiresAt())) {
            otpStore.remove(key);
            return false;
        }

        // mismatch -> fail (optional: track attempts)
        if (!constantTimeEquals(data.otp(), providedOtp)) return false;

        // success -> one-time use
        otpStore.remove(key);
        return true;
    }

    public void invalidate(String key) {
        otpStore.remove(key);
    }

    private String generate6DigitOtp() {
        int otp = RANDOM.nextInt(1_000_000);
        return String.format("%06d", otp);
    }

    // Cleanup expired OTPs every 30 seconds
    @Scheduled(fixedDelay = 30_000)
    public void cleanupExpiredOtps() {
        Instant now = Instant.now();
        otpStore.entrySet().removeIf(e -> now.isAfter(e.getValue().expiresAt()));
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        int diff = a.length() ^ b.length();
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) diff |= a.charAt(i) ^ b.charAt(i);
        return diff == 0;
    }

    public record OtpData(String otp, Instant expiresAt) {}
}
