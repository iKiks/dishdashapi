package com.kiks.dishdashapi.controller;

import com.kiks.dishdashapi.model.User;
import com.kiks.dishdashapi.service.JwtService;
import com.kiks.dishdashapi.service.OtpService;
import com.kiks.dishdashapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {
    public record OtpRequest(String email) {}


    private final UserService service;

    private final JwtService jwtService;

    private final OtpService otpService;

    final
    AuthenticationManager authenticationManager;

    public UserController(UserService service, JwtService jwtService, OtpService otpService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return service.saveUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getEmail());
        else
            return "Login Failed";

    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest req) {
        String email = (req.email() == null) ? null : req.email().trim().toLowerCase();

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        // Security: don’t reveal existence
        if (!service.existByEmail(email)) {
            return ResponseEntity.ok("If the email exists, an OTP has been sent.");
        }

        String key = "otp:email:" + email;

        String otp = otpService.generateAndStoreOtp(key);

        // Cooldown hit
        if (otp == null) {
            var next = otpService.getNextAllowedRequestTime(key);
            long secondsLeft = Math.max(0, java.time.Duration.between(java.time.Instant.now(), next).getSeconds());

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", String.valueOf(secondsLeft))
                    .body("Too many requests. Try again in " + secondsLeft + "s");
        }

        // TODO: send via email service; printing for dev only
        System.out.println("otp: " + otp);

        return ResponseEntity.ok("OTP sent");
    }

    public record OtpVerifyRequest(String email, String otp) {}

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest req) {
        String email = (req.email() == null) ? null : req.email().trim().toLowerCase();
        String otp = (req.otp() == null) ? null : req.otp().trim();

        if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        String key = "otp:email:" + email;

        String resetToken = otpService.verifyOtpAndIssueResetToken(key, otp);

        // null means: invalid / expired / too many attempts (OTP invalidated)
        if (resetToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid/expired OTP (or too many attempts)");
        }

        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }




}
