package com.example.springRest.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;
import com.example.springRest.Repository.AdminRepository;
import com.example.springRest.Repository.PostRepository;
import com.example.springRest.Response.AdminReq;
import com.example.springRest.Service.AdminService;

@Controller
public class AdminController {

	@Autowired
	AdminService adminService;

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	PostRepository postRepository;

	@PostMapping("/signUp")
	public String addAdmin(@ModelAttribute AdminReq req, Model model) {
		String result = adminService.addAdmin(req);

		if (!"SUCCESS".equals(result)) {
			model.addAttribute("error", result);
			return "register";
		}

		model.addAttribute("message", "Registration successful! Please login.");
		return "redirect:/login";
	}

	@GetMapping("/admin/dashboard")
	public String dashboard(Model model, Principal principal) {
		Admin currentUser = adminService.findByUsername(principal.getName());
		model.addAttribute("loggedInUser", currentUser);

		// Stats
		model.addAttribute("totalUsers",    adminRepository.count());
		model.addAttribute("totalPosts",    postRepository.count());
		model.addAttribute("pendingCount",  postRepository.countPendingPosts());
		model.addAttribute("approvedCount", postRepository.countByStatus("Approved"));
		model.addAttribute("rejectedCount", postRepository.countByStatus("Rejected"));

		// Sections
		List<Post> pendingPosts = postRepository.findPendingPosts();
		List<Post> recentPosts  = postRepository.findTop5ByOrderByCreatedDateDesc();
		List<Admin> allUsers    = adminRepository.findAll();

		model.addAttribute("pendingPosts", pendingPosts);
		model.addAttribute("recentPosts",  recentPosts);
		model.addAttribute("allUsers",     allUsers);

		return "admin-dashboard";
	}

	@PostMapping("/admin/approvePost/{postId}")
	public String approvePost(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
		Post post = postRepository.findById(postId).orElse(null);
		if (post != null) {
			post.setStatus("Approved");
			postRepository.save(post);
			redirectAttributes.addFlashAttribute("message", "Post \"" + post.getTitle() + "\" approved.");
		}
		return "redirect:/admin/dashboard";
	}

	@PostMapping("/admin/rejectPost/{postId}")
	public String rejectPost(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
		Post post = postRepository.findById(postId).orElse(null);
		if (post != null) {
			post.setStatus("Rejected");
			postRepository.save(post);
			redirectAttributes.addFlashAttribute("error", "Post \"" + post.getTitle() + "\" rejected.");
		}
		return "redirect:/admin/dashboard";
	}
}
