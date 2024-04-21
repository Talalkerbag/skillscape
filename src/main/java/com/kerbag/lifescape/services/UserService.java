package com.kerbag.lifescape.services;

import com.kerbag.lifescape.models.PendingRegistration;
import com.kerbag.lifescape.models.User;
import com.kerbag.lifescape.models.UserRegistrationDto;
import com.kerbag.lifescape.repositories.PendingRegistrationRepository;
import com.kerbag.lifescape.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User registerUser(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setVerified(false);
        return userRepository.save(user);
    }

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

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public String generateVerificationCode() {
        return UUID.randomUUID().toString();
    }
}
