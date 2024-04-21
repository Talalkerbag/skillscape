package com.kerbag.lifescape.services;

import com.kerbag.lifescape.models.PendingRegistration;
import com.kerbag.lifescape.models.UserRegistrationDto;
import com.kerbag.lifescape.repositories.PendingRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PendingRegistrationService {

    @Autowired
    private PendingRegistrationRepository pendingRegistrationRepository;

    public PendingRegistration createPendingRegistration(UserRegistrationDto registrationDto) {
        PendingRegistration pendingRegistration = new PendingRegistration();
        // Set fields from registrationDto
        // Generate and set verification code
        return pendingRegistrationRepository.save(pendingRegistration);
    }

}
