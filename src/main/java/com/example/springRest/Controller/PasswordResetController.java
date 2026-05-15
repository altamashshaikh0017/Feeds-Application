package com.example.springRest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springRest.Service.PasswordResetService;

@Controller
public class PasswordResetController {

	@Autowired
	private PasswordResetService passwordResetService;
	
	@GetMapping("/forgot-password")
	public String showResetPassPage() {
		return "forgot-password";
	}
	
	@PostMapping("/forgot-password")
	public String processForgetPassword(@RequestParam String email, Model model) {
		try {
			passwordResetService.createPasswordResetToken(email);
			model.addAttribute("message", "A password reset link has been sent to your email.");
			return "message";
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			return "forgot-password";
		}
	}
	
	@GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password"; // Thymeleaf template
    }
	
	@PostMapping("/reset-password")
	public String processResetPassword(@RequestParam String token, @RequestParam String password,
			Model model) {
		try {
			passwordResetService.resetPassword(token, password);
			model.addAttribute("message", "Password reset successfully. You can now log in.");
			return "message";
		} catch (RuntimeException e) {
			model.addAttribute("token", token);
			model.addAttribute("error", e.getMessage());
			return "reset-password";
		}
	}
}
