package com.kerbag.lifescape.controllers;

import com.kerbag.lifescape.models.PendingRegistration;
import com.kerbag.lifescape.models.User;
import com.kerbag.lifescape.models.UserRegistrationDto;
import com.kerbag.lifescape.services.EmailService;
import com.kerbag.lifescape.services.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) throws MessagingException {
        if(userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }

        // Now, we create a pending registration instead of a User
        PendingRegistration pendingRegistration = userService.registerPendingUser(registrationDto);

        // Send verification email with details from the pending registration
        emailService.sendVerificationEmail(pendingRegistration);

        return ResponseEntity.ok("User registered. Please check your email to verify your account.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {
        try {
            User verifiedUser = userService.verifyUser(code);
            if (verifiedUser != null) {
                return ResponseEntity.ok("User verified successfully.");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired verification code.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred during verification.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            String resetToken = userService.initiateResetPassword(email);
            User user = userService.findByEmail(email);
            emailService.sendPasswordResetEmail(user, resetToken);
            return ResponseEntity.ok("Reset password instructions have been sent to your email.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("User with given email does not exist.");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("Failed to send reset email.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestBody String newPassword) {
        try {
            boolean reset = userService.resetPassword(token, newPassword);
            if (reset) {
                return ResponseEntity.ok("Password has been reset successfully.");
            } else {
                return ResponseEntity.badRequest().body("Invalid or expired reset token.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while resetting the password.");
        }
    }


    @GetMapping("/health")
    public String healthCheck(){
        return "healthy";
    }
}
