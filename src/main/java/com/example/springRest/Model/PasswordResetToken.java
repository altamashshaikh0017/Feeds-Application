package com.example.springRest.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class PasswordResetToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tokenId;
	
	private String token;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pkAdminId", nullable = false)
	private Admin admin;
	
	private LocalDateTime expiryDateTime;
	
	
}
