package com.sms.Scholarship.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sms.Scholarship.model.User;
import com.sms.Scholarship.repository.UserRepository;
import com.sms.Scholarship.service.OtpService;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public UserController(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    // Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>(
                "{\"message\": \"Email already in use\"}",
                HttpStatus.CONFLICT
            );
        }

        // Generate OTP and send to email
        int otp = otpService.generateOtp();
        otpService.sendOtp(user.getEmail(), otp);

        // Temporarily store the user details for OTP verification
        otpService.saveUserForOtpVerification(user);

        return new ResponseEntity<>(
            "{\"message\": \"OTP sent to your email. Please verify to complete signup.\"}",
            HttpStatus.OK
        );
    }

    // OTP verification endpoint
    @PostMapping("/signup/verify")
    public ResponseEntity<Object> verifyOtp(@RequestParam String email, @RequestParam int otp) {
        if (!otpService.hasAttemptsLeft()) {
            return new ResponseEntity<>(
                "{\"message\": \"Maximum attempts exceeded. Please try signing up again.\"}",
                HttpStatus.FORBIDDEN
            );
        }

        if (otpService.validateOtp(otp)) {
            otpService.resetAttempts();
            
            // Retrieve the temporarily stored user based on their email
            User user = otpService.getUserForOtpVerification(email);

            if (user == null) {
                return new ResponseEntity<>(
                    "{\"message\": \"No user found for verification. Please try again.\"}",
                    HttpStatus.BAD_REQUEST
                );
            }

            // Save the user to the database after successful OTP validation
            userRepository.save(user);
            
            return new ResponseEntity<>(
                "{\"message\": \"Signup successful. You can now log in.\"}",
                HttpStatus.OK
            );
        } else {
            otpService.incrementAttempts();
            return new ResponseEntity<>(
                "{\"message\": \"Invalid OTP. Attempts left: " + (3 - otpService.getAttempts()) + "\"}",
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail());
        if (user == null || !user.getPassword().equals(loginUser.getPassword())) {
            return new ResponseEntity<>(
                "{\"message\": \"Invalid credentials\"}",
                HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
            "{\"message\": \"Login successful\"}",
            HttpStatus.OK
        );
    }
}
