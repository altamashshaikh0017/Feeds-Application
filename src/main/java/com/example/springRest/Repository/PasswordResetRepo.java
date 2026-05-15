package com.example.springRest.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springRest.Model.PasswordResetToken;

@Repository
public interface PasswordResetRepo extends JpaRepository<PasswordResetToken, Long>{
	PasswordResetToken findByToken(String token);

}
