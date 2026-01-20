package com.kiks.dishdashapi.service;

import java.security.SecureRandom;

public class OtpService {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate6DigitOtp() {
        int otp = RANDOM.nextInt(1_000_000); // 0 - 999999
        return String.format("%06d", otp);
    }

}
