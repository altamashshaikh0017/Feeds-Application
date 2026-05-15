package com.example.springRest.Service;

import java.time.LocalDateTime;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.PasswordResetToken;
import com.example.springRest.Repository.AdminRepository;
import com.example.springRest.Repository.PasswordResetRepo;

@Service
public class PasswordResetService {

	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	PasswordResetRepo passwordResetRepo;
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void createPasswordResetToken(String email) {
		
		Admin admin = adminRepository.findByEmail(email).orElseThrow(()-> 
		new RuntimeException("No account found with email : " + email));
		
		String token = UUID.randomUUID().toString();
		
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setToken(token);
		passwordResetToken.setAdmin(admin);
		passwordResetToken.setExpiryDateTime(LocalDateTime.now().plusHours(1)); // token valid for 1 hr 
		passwordResetRepo.save(passwordResetToken);
		
		//send email
		
		String resetPassLink = "http://localhost:8080/reset-password?token=" + token;
		sendEmail(admin.getEmail(), resetPassLink);
	}

	private void sendEmail(String email, String resetLink) {
		SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetLink);
        mailSender.send(message);
		
	}
	
	public void resetPassword(String token, String newPassword) {
		PasswordResetToken resetToken = passwordResetRepo.findByToken(token);

        if (resetToken == null || resetToken.getExpiryDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired password reset token");
        }

        Admin admin = resetToken.getAdmin();
        admin.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        adminRepository.save(admin);

        // Optional: delete token after use
        passwordResetRepo.delete(resetToken);
	}
}
