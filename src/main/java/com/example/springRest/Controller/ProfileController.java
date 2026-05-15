package com.example.springRest.Controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;
import com.example.springRest.Repository.AdminRepository;
import com.example.springRest.Repository.PostCommentRepository;
import com.example.springRest.Repository.PostLikeRepository;
import com.example.springRest.Repository.PostRepository;
import com.example.springRest.Response.ProfileUpdateReq;
import com.example.springRest.Service.AdminService;
import com.example.springRest.Service.CloudinaryService;

@Controller
public class ProfileController {

	@Autowired private AdminService        adminService;
	@Autowired private CloudinaryService   cloudinaryService;
	@Autowired private AdminRepository     adminRepository;
	@Autowired private PostRepository      postRepository;
	@Autowired private PostLikeRepository  postLikeRepository;
	@Autowired private PostCommentRepository postCommentRepository;

	@GetMapping("/profile")
	public String ownProfile(Principal principal) {
		return "redirect:/profile/" + principal.getName();
	}

	@GetMapping("/profile/{username}")
	public String viewProfile(@PathVariable String username, Model model, Principal principal) {
		Admin profileUser = adminService.findByUsername(username);
		if (profileUser == null) return "redirect:/home";

		Admin currentUser = adminService.findByUsername(principal.getName());
		boolean isOwnProfile = currentUser.getUserName().equals(username);
		boolean isAdmin      = "ADMIN".equalsIgnoreCase(currentUser.getRole());

		// Stats
		long postCount        = postRepository.countByAuthor(profileUser);
		long likesReceived    = postLikeRepository.countLikesReceivedByAuthor(profileUser);
		long commentsReceived = postCommentRepository.countCommentsReceivedByAuthor(profileUser);

		// Recent posts — own/admin see all, others see approved only
		List<Post> recentPosts = (isOwnProfile || isAdmin)
				? postRepository.findByAuthorOrderByCreatedDateDesc(profileUser, PageRequest.of(0, 6)).getContent()
				: postRepository.findByAuthorAndStatusOrderByCreatedDateDesc(profileUser, "Approved", PageRequest.of(0, 6)).getContent();

		model.addAttribute("profileUser",      profileUser);
		model.addAttribute("isOwnProfile",     isOwnProfile);
		model.addAttribute("loggedInUser",      currentUser);
		model.addAttribute("postCount",         postCount);
		model.addAttribute("likesReceived",     likesReceived);
		model.addAttribute("commentsReceived",  commentsReceived);
		model.addAttribute("recentPosts",       recentPosts);
		model.addAttribute("profileInitials",   initials(profileUser.getName()));
		return "profile";
	}

	@GetMapping("/profile/edit")
	public String showEditProfile(Model model, Principal principal) {
		Admin currentUser = adminService.findByUsername(principal.getName());
		model.addAttribute("profileUser",     currentUser);
		model.addAttribute("profileInitials", initials(currentUser.getName()));
		model.addAttribute("req",             toReq(currentUser));
		return "edit-profile";
	}

	@PostMapping("/profile/edit")
	public String saveProfile(@ModelAttribute("req") ProfileUpdateReq req,
			Principal principal, RedirectAttributes ra,
			@RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
		if (profilePicture != null && !profilePicture.isEmpty()) {
			try {
				req.setProfilePictureUrl(cloudinaryService.uploadFile(profilePicture));
			} catch (Exception e) {
				ra.addFlashAttribute("error", "Profile picture upload failed. Other changes were saved.");
			}
		}
		adminService.updateProfile(principal.getName(), req);
		ra.addFlashAttribute("message", "Profile updated successfully!");
		return "redirect:/profile";
	}

	@PostMapping("/profile/changePassword")
	public String changePassword(Principal principal,
			@RequestParam String currentPassword,
			@RequestParam String newPassword,
			@RequestParam String confirmPassword,
			RedirectAttributes ra) {
		String result = adminService.changePassword(
				principal.getName(), currentPassword, newPassword, confirmPassword);
		if ("SUCCESS".equals(result)) {
			ra.addFlashAttribute("pwMessage", "Password changed successfully!");
		} else {
			ra.addFlashAttribute("pwError", result);
		}
		return "redirect:/profile/edit";
	}

	// ── helpers ──────────────────────────────────────────────────────────────

	private static String initials(String name) {
		if (name == null || name.isBlank()) return "?";
		return Arrays.stream(name.trim().split("\\s+"))
				.map(w -> String.valueOf(w.charAt(0)).toUpperCase())
				.limit(2)
				.collect(Collectors.joining());
	}

	private static ProfileUpdateReq toReq(Admin a) {
		ProfileUpdateReq r = new ProfileUpdateReq();
		r.setName(a.getName());
		r.setBio(a.getBio());
		r.setProfilePictureUrl(a.getProfilePictureUrl());
		r.setWebsite(a.getWebsite());
		r.setLocation(a.getLocation());
		r.setGender(a.getGender());
		r.setDateOfBirth(a.getDateOfBirth());
		r.setContactNumber(a.getContactNumber());
		return r;
	}
}
