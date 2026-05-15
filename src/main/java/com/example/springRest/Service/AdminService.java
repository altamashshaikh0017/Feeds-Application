package com.example.springRest.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springRest.Model.Admin;
import com.example.springRest.Repository.AdminRepository;
import com.example.springRest.Response.AdminReq;
import com.example.springRest.Response.ProfileUpdateReq;

@Service
public class AdminService {

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	public String addAdmin(AdminReq req) {
		try {
			if (req.getName() == null || req.getName().isEmpty() ||
				req.getEmail() == null || req.getEmail().isEmpty() ||
				req.getUsername() == null || req.getUsername().isEmpty() ||
				req.getPassword() == null || req.getPassword().isEmpty() ||
				req.getRole() == null || req.getRole().isEmpty() ||
				req.getContactNumber() == null || req.getContactNumber().isEmpty()) {
				return "All required fields must be filled.";
			}

			Admin admin = new Admin();
			admin.setName(req.getName());
			admin.setEmail(req.getEmail());
			admin.setUserName(req.getUsername());
			admin.setAddress(req.getAddress());
			admin.setRole(req.getRole().toUpperCase());
			admin.setContactNumber(req.getContactNumber());
			admin.setPassword(passwordEncoder.encode(req.getPassword()));
			admin.setJoinedDate(LocalDateTime.now());

			adminRepository.save(admin);
			return "SUCCESS";

		} catch (Exception e) {
			e.printStackTrace();
			return "An error occurred while adding admin.";
		}
	}

	public Admin findByUsername(String userName) {
		return adminRepository.findByUserName(userName).orElse(null);
	}

	public void updateProfile(String userName, ProfileUpdateReq req) {
		Admin admin = adminRepository.findByUserName(userName).orElseThrow();
		if (req.getName() != null && !req.getName().isBlank())
			admin.setName(req.getName());
		admin.setBio(req.getBio());
		admin.setProfilePictureUrl(req.getProfilePictureUrl());
		admin.setWebsite(req.getWebsite());
		admin.setLocation(req.getLocation());
		admin.setGender(req.getGender());
		admin.setDateOfBirth(req.getDateOfBirth());
		admin.setContactNumber(req.getContactNumber());
		adminRepository.save(admin);
	}

	public String changePassword(String userName, String currentPassword,
			String newPassword, String confirmPassword) {
		Admin admin = adminRepository.findByUserName(userName).orElseThrow();
		if (!passwordEncoder.matches(currentPassword, admin.getPassword()))
			return "Current password is incorrect.";
		if (!newPassword.equals(confirmPassword))
			return "New passwords do not match.";
		if (newPassword.length() < 6)
			return "New password must be at least 6 characters.";
		admin.setPassword(passwordEncoder.encode(newPassword));
		adminRepository.save(admin);
		return "SUCCESS";
	}
}
