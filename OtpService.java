
package com.sms.Scholarship.service;
import com.sms.Scholarship.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    private Map<String, User> userOtpStorage = new HashMap<>();
    private Map<String, Integer> otpStorage = new HashMap<>();
    private int attempts = 0;
    private final int maxAttempts = 3;

    // Generate OTP
    public int generateOtp() {
        // Generate a 6-digit OTP
        return (int)(Math.random() * 900000) + 100000;
    }

    // Send OTP to user's email (you would use email service here)
    public void sendOtp(String email, int otp) {
        // Simulate sending OTP to email
        otpStorage.put(email, otp);
        System.out.println("OTP sent to " + email + ": " + otp); // For testing purposes
    }

    // Store user for OTP verification
    public void saveUserForOtpVerification(User user) {
        userOtpStorage.put(user.getEmail(), user);
    }

    // Retrieve user for OTP verification
    public User getUserForOtpVerification(String email) {
        return userOtpStorage.get(email);
    }

    // Validate the OTP
    public boolean validateOtp(int otp) {
        return otpStorage.containsValue(otp);
    }

    // Manage OTP attempts
    public boolean hasAttemptsLeft() {
        return attempts < maxAttempts;
    }

    public void incrementAttempts() {
        attempts++;
    }

    public void resetAttempts() {
        attempts = 0;
    }

    public int getAttempts() {
        return attempts;
    }
}
