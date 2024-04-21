package com.kerbag.lifescape.repositories;

import com.kerbag.lifescape.models.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, UUID> {
    PendingRegistration findByVerificationCode(String verificationCode);

    Optional<PendingRegistration> findByEmail(String email);
}