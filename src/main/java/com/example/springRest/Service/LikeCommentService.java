package com.example.springRest.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;
import com.example.springRest.Model.PostComment;
import com.example.springRest.Model.PostLike;
import com.example.springRest.Repository.AdminRepository;
import com.example.springRest.Repository.PostCommentRepository;
import com.example.springRest.Repository.PostLikeRepository;
import com.example.springRest.Repository.PostRepository;
import org.springframework.context.annotation.Lazy;

@Service
public class LikeCommentService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Autowired
	private PostCommentRepository postCommentRepository;

	@Autowired @Lazy
	private NotificationService notificationService;

	@Transactional
	public void toggleLike(Long postId, String userName) {
		Post post   = postRepository.findById(postId).orElseThrow();
		Admin admin = adminRepository.findByUserName(userName).orElseThrow();

		Optional<PostLike> existing = postLikeRepository.findByPostAndLikedBy(post, admin);
		if (existing.isPresent()) {
			postLikeRepository.delete(existing.get());
		} else {
			PostLike like = new PostLike();
			like.setPost(post);
			like.setLikedBy(admin);
			like.setLikedAt(LocalDateTime.now());
			postLikeRepository.save(like);
			notificationService.notifyLike(post, admin);
		}
	}

	@Transactional
	public void addComment(Long postId, String userName, String content) {
		if (content == null || content.trim().isEmpty()) return;
		Post post   = postRepository.findById(postId).orElseThrow();
		Admin admin = adminRepository.findByUserName(userName).orElseThrow();

		PostComment comment = new PostComment();
		comment.setPost(post);
		comment.setCommentedBy(admin);
		comment.setContent(content.trim());
		comment.setCommentedAt(LocalDateTime.now());
		postCommentRepository.save(comment);
		notificationService.notifyComment(post, admin, content.trim());
	}

	@Transactional
	public void deleteComment(Long commentId, String userName) {
		PostComment comment = postCommentRepository.findById(commentId).orElse(null);
		if (comment == null) return;

		Admin requestingUser = adminRepository.findByUserName(userName).orElse(null);
		boolean isOwner = comment.getCommentedBy().getUserName().equals(userName);
		boolean isAdmin = requestingUser != null && "ADMIN".equalsIgnoreCase(requestingUser.getRole());

		if (isOwner || isAdmin) {
			postCommentRepository.delete(comment);
		}
	}

	public List<PostLike> getLikesForPost(Post post) {
		return postLikeRepository.findByPostOrderByLikedAtDesc(post);
	}

	public List<PostComment> getCommentsForPost(Post post) {
		return postCommentRepository.findByPostOrderByCommentedAtDesc(post);
	}

	public long getLikeCount(Post post) {
		return postLikeRepository.countByPost(post);
	}

	public long getCommentCount(Post post) {
		return postCommentRepository.countByPost(post);
	}

	public boolean hasUserLiked(Post post, Admin admin) {
		return postLikeRepository.existsByPostAndLikedBy(post, admin);
	}
}
