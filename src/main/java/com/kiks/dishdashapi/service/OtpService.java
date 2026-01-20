package com.kiks.dishdashapi.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Duration OTP_TTL = Duration.ofMinutes(1);
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(10);

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private final Map<String, ResetTokenData> resetTokenStore = new ConcurrentHashMap<>();

    /* ================= OTP ================= */

    public String generateAndStoreOtp(String key) {
        String otp = generate6DigitOtp();
        Instant expiresAt = Instant.now().plus(OTP_TTL);
        otpStore.put(key, new OtpData(otp, expiresAt));
        return otp;
    }

    /**
     * Verifies OTP and returns a reset UUID if valid.
     * Returns null if invalid or expired.
     */
    public String verifyOtpAndIssueResetToken(String key, String providedOtp) {
        OtpData data = otpStore.get(key);
        if (data == null) return null;

        if (Instant.now().isAfter(data.expiresAt())) {
            otpStore.remove(key);
            return null;
        }

        if (!constantTimeEquals(data.otp(), providedOtp)) {
            return null;
        }

        // OTP is valid → destroy it
        otpStore.remove(key);

        // Issue reset token
        String resetToken = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(RESET_TOKEN_TTL);
        resetTokenStore.put(resetToken, new ResetTokenData(key, expiresAt));

        return resetToken;
    }

    /* ================= RESET TOKEN ================= */

    public boolean verifyResetToken(String resetToken, String key) {
        ResetTokenData data = resetTokenStore.get(resetToken);
        if (data == null) return false;

        if (Instant.now().isAfter(data.expiresAt())) {
            resetTokenStore.remove(resetToken);
            return false;
        }

        if (!data.key().equals(key)) return false;

        // one-time use
        resetTokenStore.remove(resetToken);
        return true;
    }

    /* ================= Utils ================= */

    private String generate6DigitOtp() {
        int otp = RANDOM.nextInt(1_000_000);
        return String.format("%06d", otp);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        int diff = a.length() ^ b.length();
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) diff |= a.charAt(i) ^ b.charAt(i);
        return diff == 0;
    }

    /* ================= Cleanup ================= */

    @Scheduled(fixedDelay = 30_000)
    public void cleanupExpired() {
        Instant now = Instant.now();

        otpStore.entrySet().removeIf(e ->
                now.isAfter(e.getValue().expiresAt()));

        resetTokenStore.entrySet().removeIf(e ->
                now.isAfter(e.getValue().expiresAt()));
    }

    /* ================= Records ================= */

    public record OtpData(String otp, Instant expiresAt) {}

    public record ResetTokenData(String key, Instant expiresAt) {}
}
