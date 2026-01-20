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

    // OTP expires fast
    private static final Duration OTP_TTL = Duration.ofMinutes(1);

    // Reset token lasts a bit longer
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(10);

    // Security controls
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration REQUEST_COOLDOWN = Duration.ofSeconds(60);

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private final Map<String, ResetTokenData> resetTokenStore = new ConcurrentHashMap<>();
    private final Map<String, CooldownData> cooldownStore = new ConcurrentHashMap<>();

    /* ================= OTP REQUEST ================= */

    /**
     * Generates and stores OTP if cooldown passed.
     * @return otp (for now) or null if still cooling down
     */
    public String generateAndStoreOtp(String key) {
        Instant now = Instant.now();

        // Cooldown check
        CooldownData cd = cooldownStore.get(key);
        if (cd != null && now.isBefore(cd.nextAllowedAt())) {
            return null; // still cooling down
        }

        String otp = generate6DigitOtp();
        Instant expiresAt = now.plus(OTP_TTL);

        otpStore.put(key, new OtpData(otp, expiresAt, MAX_ATTEMPTS));

        // Start cooldown window
        cooldownStore.put(key, new CooldownData(now.plus(REQUEST_COOLDOWN)));

        return otp;
    }

    public Instant getNextAllowedRequestTime(String key) {
        CooldownData cd = cooldownStore.get(key);
        return (cd == null) ? Instant.EPOCH : cd.nextAllowedAt();
    }

    /* ================= OTP VERIFY ================= */

    /**
     * Verifies OTP; if valid, returns a reset UUID.
     * - Wrong attempts decrement; after MAX_ATTEMPTS failures -> OTP invalidated.
     * - Expired OTP removed.
     * Returns null if invalid/expired/too many attempts.
     */
    public String verifyOtpAndIssueResetToken(String key, String providedOtp) {
        Instant now = Instant.now();

        final Holder<String> issued = new Holder<>(null);

        otpStore.compute(key, (k, data) -> {
            if (data == null) return null;

            // Expired -> delete
            if (now.isAfter(data.expiresAt())) return null;

            // Wrong OTP -> decrement attempts, possibly delete
            if (!constantTimeEquals(data.otp(), providedOtp)) {
                int left = data.attemptsLeft() - 1;
                if (left <= 0) return null; // invalidate OTP
                return new OtpData(data.otp(), data.expiresAt(), left);
            }

            // Correct OTP -> delete OTP and issue reset token
            String resetToken = UUID.randomUUID().toString();
            resetTokenStore.put(resetToken, new ResetTokenData(key, now.plus(RESET_TOKEN_TTL)));
            issued.value = resetToken;

            return null; // remove OTP (one-time use)
        });

        return issued.value;
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

        resetTokenStore.remove(resetToken); // one-time use
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

        otpStore.entrySet().removeIf(e -> now.isAfter(e.getValue().expiresAt()));
        resetTokenStore.entrySet().removeIf(e -> now.isAfter(e.getValue().expiresAt()));
        cooldownStore.entrySet().removeIf(e -> now.isAfter(e.getValue().nextAllowedAt()));
    }

    /* ================= Records ================= */

    public record OtpData(String otp, Instant expiresAt, int attemptsLeft) {}

    public record ResetTokenData(String key, Instant expiresAt) {}

    public record CooldownData(Instant nextAllowedAt) {}

    // Tiny holder so we can “return” a value from inside compute()
    private static final class Holder<T> {
        T value;
        Holder(T value) { this.value = value; }
    }
}
