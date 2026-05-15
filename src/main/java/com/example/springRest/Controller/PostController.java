package com.example.springRest.Controller;

import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.example.springRest.Model.PostComment;
import com.example.springRest.Model.PostLike;
import com.example.springRest.Repository.PostRepository;
import com.example.springRest.Response.PostReq;
import com.example.springRest.Service.AdminService;
import com.example.springRest.Service.CloudinaryService;
import com.example.springRest.Service.LikeCommentService;
import com.example.springRest.Service.PostService;

@Controller
public class PostController {

	private static final Logger logger = LogManager.getLogger(PostController.class);
	private static final int PAGE_SIZE = 5;

	@Autowired
	PostService postService;

	@Autowired
	AdminService adminService;

	@Autowired
	PostRepository postRepository;

	@Autowired
	LikeCommentService likeCommentService;

	@Autowired
	CloudinaryService cloudinaryService;

	@GetMapping("/createPost")
	public String showCreatePost(Model model, Principal principal) {
		model.addAttribute("post", new PostReq());
		Admin currentUser = adminService.findByUsername(principal.getName());
		model.addAttribute("currentUser", currentUser);
		logger.info("Inside Post Controller::showCreatePost()");
		return "createPost";
	}

	@PostMapping("/createPost")
	public String submitPost(@ModelAttribute PostReq postReq, Model model, Principal principal,
			@RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
		String coverImageUrl = null;
		if (coverImage != null && !coverImage.isEmpty()) {
			try {
				coverImageUrl = cloudinaryService.uploadFile(coverImage);
			} catch (Exception e) {
				model.addAttribute("error", "Image upload failed. Please try again.");
				model.addAttribute("post", postReq);
				return "createPost";
			}
		}
		String result = postService.addPost(postReq, principal.getName(), coverImageUrl);
		if (!"SUCCESS".equals(result)) {
			model.addAttribute("error", result);
			model.addAttribute("post", postReq);
			return "createPost";
		}
		return "redirect:/viewPosts";
	}

	@GetMapping("/viewPosts")
	public String viewPosts(Model model, Principal principal,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page) {
		logger.info("Inside Post Controller::viewPosts()");
		Admin currentUser = adminService.findByUsername(principal.getName());
		boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());

		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Post> postPage;

		if (search != null && !search.trim().isEmpty()) {
			String q = search.trim();
			postPage = isAdmin
					? postRepository.searchAllPosts(q, pageable)
					: postRepository.searchPostsByAuthor(q, currentUser, pageable);
			model.addAttribute("search", q);
		} else {
			postPage = isAdmin
					? postService.getAllPosts(pageable)
					: postService.getPostsByAuthor(currentUser, pageable);
		}

		addPaginationAttributes(model, postPage, page);

		List<Post> postList = postPage.getContent();
		Map<Long, Long> likeCountMap    = new LinkedHashMap<>();
		Map<Long, Long> commentCountMap = new LinkedHashMap<>();
		Map<Long, List<PostLike>>    likesMap    = new LinkedHashMap<>();
		Map<Long, List<PostComment>> commentsMap = new LinkedHashMap<>();
		for (Post post : postList) {
			Long id = post.getPkPostId();
			likeCountMap.put(id,    likeCommentService.getLikeCount(post));
			commentCountMap.put(id, likeCommentService.getCommentCount(post));
			likesMap.put(id,        likeCommentService.getLikesForPost(post));
			commentsMap.put(id,     likeCommentService.getCommentsForPost(post));
		}
		model.addAttribute("posts",          postList);
		model.addAttribute("likeCountMap",   likeCountMap);
		model.addAttribute("commentCountMap",commentCountMap);
		model.addAttribute("likesMap",       likesMap);
		model.addAttribute("commentsMap",    commentsMap);
		model.addAttribute("loggedInUser",   currentUser);
		return "viewPosts";
	}

	@GetMapping("/viewOtherUsersPosts")
	public String viewOtherUsersPosts(Model model, Principal principal,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page) {
		logger.info("Inside Post Controller::viewOtherUsersPosts()");
		Admin currentUser = adminService.findByUsername(principal.getName());
		boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());

		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Post> postPage;

		if (search != null && !search.trim().isEmpty()) {
			String q = search.trim();
			postPage = isAdmin
					? postRepository.searchApprovedPosts(q, pageable)
					: postRepository.searchApprovedPostsExcludingAuthor(q, currentUser, pageable);
			model.addAttribute("search", q);
		} else {
			postPage = isAdmin
					? postRepository.findAllByStatus("Approved", pageable)
					: postRepository.findByStatusAndAuthorNot("Approved", currentUser, pageable);
		}

		addPaginationAttributes(model, postPage, page);

		List<Post> postList = postPage.getContent();
		Set<Long> likedPostIds          = new HashSet<>();
		Map<Long, Long> likeCountMap    = new LinkedHashMap<>();
		Map<Long, Long> commentCountMap = new LinkedHashMap<>();
		Map<Long, List<PostComment>> commentsMap = new LinkedHashMap<>();
		for (Post post : postList) {
			Long id = post.getPkPostId();
			likeCountMap.put(id,    likeCommentService.getLikeCount(post));
			commentCountMap.put(id, likeCommentService.getCommentCount(post));
			commentsMap.put(id,     likeCommentService.getCommentsForPost(post));
			if (likeCommentService.hasUserLiked(post, currentUser)) {
				likedPostIds.add(id);
			}
		}
		model.addAttribute("posts",          postList);
		model.addAttribute("likedPostIds",   likedPostIds);
		model.addAttribute("likeCountMap",   likeCountMap);
		model.addAttribute("commentCountMap",commentCountMap);
		model.addAttribute("commentsMap",    commentsMap);
		model.addAttribute("loggedInUser",   currentUser);
		return "viewOtherUsersPosts";
	}

	private void addPaginationAttributes(Model model, Page<Post> postPage, int currentPage) {
		model.addAttribute("currentPage",  currentPage);
		model.addAttribute("totalPages",   postPage.getTotalPages());
		model.addAttribute("totalItems",   postPage.getTotalElements());
		model.addAttribute("pageSize",     PAGE_SIZE);
		model.addAttribute("hasPrev",      postPage.hasPrevious());
		model.addAttribute("hasNext",      postPage.hasNext());
	}

	@GetMapping("/deletePost/{postId}")
	public String deletePost(@PathVariable Long postId, Principal principal,
			RedirectAttributes redirectAttributes) {
		logger.info("Inside Post Controller::deletePost()");
		try {
			Post post = postService.getPostById(postId);
			Admin currentUser = adminService.findByUsername(principal.getName());
			if (post != null && (
					post.getAuthor().getUserName().equals(principal.getName()) ||
					currentUser.getRole().equalsIgnoreCase("ADMIN"))) {
				postService.deletePost(postId);
				redirectAttributes.addFlashAttribute("message", "Post deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this post.");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error deleting post.");
		}
		return "redirect:/viewPosts";
	}

	@GetMapping("/editPost/{postId}")
	public String showEditPostForm(@PathVariable Long postId, Model model, Principal principal) {
		Post post = postService.getPostById(postId);
		Admin currentUser = adminService.findByUsername(principal.getName());
		model.addAttribute("currentUserRole", currentUser.getRole());
		model.addAttribute("currentUser", currentUser);

		if (post == null || (!post.getAuthor().getUserName().equals(currentUser.getUserName()) &&
				!currentUser.getRole().equalsIgnoreCase("ADMIN"))) {
			return "redirect:/viewPosts";
		}

		PostReq postReq = new PostReq();
		postReq.setPkPostId(post.getPkPostId());
		postReq.setTitle(post.getTitle());
		postReq.setContent(post.getContent());
		postReq.setAuthor(post.getAuthor());
		postReq.setCreatedDate(post.getCreatedDate());
		postReq.setStatus(post.getStatus());
		postReq.setCoverImageUrl(post.getCoverImageUrl());

		model.addAttribute("postReq", postReq);
		return "editPost";
	}

	@PostMapping("/updatePost")
	public String updatePost(@ModelAttribute("postReq") PostReq postReq, Principal principal,
			@RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
		Admin currentUser = adminService.findByUsername(principal.getName());
		Post existingPost = postService.getPostById(postReq.getPkPostId());

		if (existingPost == null ||
				(!existingPost.getAuthor().getUserName().equals(currentUser.getUserName())
				&& !currentUser.getRole().equalsIgnoreCase("ADMIN"))) {
			logger.info("Unauthorized update attempt by user: {}", currentUser.getUserName());
			return "redirect:/viewPosts";
		}

		existingPost.setTitle(postReq.getTitle());
		existingPost.setContent(postReq.getContent());
		existingPost.setStatus(postReq.getStatus());
		existingPost.setCreatedDate(new Date());

		if (coverImage != null && !coverImage.isEmpty()) {
			try {
				existingPost.setCoverImageUrl(cloudinaryService.uploadFile(coverImage));
			} catch (Exception e) {
				logger.warn("Cover image upload failed during post update: {}", e.getMessage());
			}
		}

		postRepository.save(existingPost);
		return "redirect:/viewPosts";
	}
}
