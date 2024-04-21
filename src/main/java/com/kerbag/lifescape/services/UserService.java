package com.kerbag.lifescape.services;

import com.kerbag.lifescape.models.PendingRegistration;
import com.kerbag.lifescape.models.User;
import com.kerbag.lifescape.models.UserRegistrationDto;
import com.kerbag.lifescape.repositories.PendingRegistrationRepository;
import com.kerbag.lifescape.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private PendingRegistrationRepository pendingRegistrationRepository;

    public PendingRegistration registerPendingUser(UserRegistrationDto registrationDto) {
        // Check if there is already a pending registration with the given email
        Optional<PendingRegistration> existingRegistration = pendingRegistrationRepository.findByEmail(registrationDto.getEmail());

        PendingRegistration pendingRegistration = existingRegistration.orElse(new PendingRegistration());
        pendingRegistration.setEmail(registrationDto.getEmail());
        pendingRegistration.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        pendingRegistration.setFirstName(registrationDto.getFirstName());
        pendingRegistration.setLastName(registrationDto.getLastName());
        pendingRegistration.setVerificationCode(generateVerificationCode()); // Ensure this method generates a secure code

        return pendingRegistrationRepository.save(pendingRegistration);
    }

    public User verifyUser(String verificationCode) {
        PendingRegistration pendingReg = pendingRegistrationRepository.findByVerificationCode(verificationCode);
        if (pendingReg != null) {
            User newUser = new User();
            newUser.setEmail(pendingReg.getEmail());
            newUser.setPassword(pendingReg.getPassword()); // Password is already hashed
            newUser.setFirstName(pendingReg.getFirstName());
            newUser.setLastName(pendingReg.getLastName());
            // Set other necessary fields from pendingReg to newUser
            userRepository.save(newUser);

            pendingRegistrationRepository.delete(pendingReg);

            return newUser;
        }
        return null;
    }

    public String initiateResetPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String resetToken = UUID.randomUUID().toString();
        // Save the token in the database, associated with the user
        saveResetTokenForUser(user, resetToken);
        return resetToken;
    }

    private void saveResetTokenForUser(User user, String resetToken) {
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));  // Set the expiry 24 hours from now
        userRepository.save(user);
    }


    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user != null && tokenIsValid(token)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null); // Clear the token after use
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private boolean tokenIsValid(String token) {
        User user = userRepository.findByResetToken(token);
        return user != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now());
    }


    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public String generateVerificationCode() {
        return UUID.randomUUID().toString();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
