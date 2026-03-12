package com.assessment.miniwallet.repository;

import com.assessment.miniwallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsById(String id);
    boolean existsByEmail(String email);
}