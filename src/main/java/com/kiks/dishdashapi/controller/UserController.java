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
        String email = req.email();

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (!service.existByEmail(email)) {
            // Security best practice: don't reveal if email exists.
            // But if you want to reveal, swap this to 404.
            return ResponseEntity.ok("If the email exists, an OTP has been sent.");
        }

        String key = "otp:email:" + email.toLowerCase();
        String otp = otpService.generateAndStoreOtp(key);

        // TODO: send email here (MailService)
        // mailService.sendOtp(email, otp);
        System.out.println("otp: " + otp);

        return ResponseEntity.ok("OTP sent");
    }

    public record OtpVerifyRequest(String email, String otp) {}

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest req) {

        String key = "otp:email:" + req.email().toLowerCase();
        String resetToken =
                otpService.verifyOtpAndIssueResetToken(key, req.otp());

        if (resetToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired OTP");
        }

        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }




}
