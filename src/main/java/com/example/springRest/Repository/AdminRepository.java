package com.example.springRest.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springRest.Model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

	Optional<Admin> findByUserName(String username);
	
	Optional<Admin> findByEmail(String email);
}
