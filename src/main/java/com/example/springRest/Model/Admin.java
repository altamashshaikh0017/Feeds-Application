package com.example.springRest.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pkAdminId;

	private String name;
	private String userName;
	private String email;
	private String password;
	private String address;
	private String role;
	private String contactNumber;

	// ── Social profile fields ──────────────────────────────
	@Column(columnDefinition = "TEXT")
	private String bio;

	private String profilePictureUrl;
	private String website;
	private String location;
	private String gender;
	private LocalDate dateOfBirth;
	private LocalDateTime joinedDate;
}
