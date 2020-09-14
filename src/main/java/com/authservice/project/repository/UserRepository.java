package com.authservice.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authservice.project.model.DBuser;

@Repository
public interface UserRepository extends JpaRepository<DBuser, Long> {
	
	DBuser findByUsername(String username);
	Boolean existsByUserName(String username);
	Boolean existsByEmail(String email);
} 
