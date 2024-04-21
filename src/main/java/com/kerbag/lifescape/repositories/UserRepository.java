package com.kerbag.lifescape.repositories;

import com.kerbag.lifescape.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}

