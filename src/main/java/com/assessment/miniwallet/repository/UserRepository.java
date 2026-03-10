package com.assessment.miniwallet.repository;

import com.assessment.miniwallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}