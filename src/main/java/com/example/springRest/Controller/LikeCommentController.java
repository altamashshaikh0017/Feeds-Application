package com.example.springRest.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.springRest.Service.LikeCommentService;

@Controller
public class LikeCommentController {

	@Autowired
	private LikeCommentService likeCommentService;

	@PostMapping("/like/{postId}")
	public String toggleLike(@PathVariable Long postId, Principal principal,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String search,
			RedirectAttributes ra) {
		likeCommentService.toggleLike(postId, principal.getName());
		return buildRedirect("/viewOtherUsersPosts", page, search, ra);
	}

	@PostMapping("/comment/{postId}")
	public String addComment(@PathVariable Long postId, Principal principal,
			@RequestParam String content,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String search,
			RedirectAttributes ra) {
		likeCommentService.addComment(postId, principal.getName(), content);
		return buildRedirect("/viewOtherUsersPosts", page, search, ra);
	}

	@PostMapping("/deleteComment/{commentId}")
	public String deleteComment(@PathVariable Long commentId, Principal principal,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String search,
			RedirectAttributes ra) {
		likeCommentService.deleteComment(commentId, principal.getName());
		return buildRedirect("/viewOtherUsersPosts", page, search, ra);
	}

	private String buildRedirect(String path, int page, String search, RedirectAttributes ra) {
		ra.addAttribute("page", page);
		if (search != null && !search.isBlank()) {
			ra.addAttribute("search", search);
		}
		return "redirect:" + path;
	}
}
