package com.example.springRest.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;
import com.example.springRest.Repository.AdminRepository;
import com.example.springRest.Repository.PostRepository;
import com.example.springRest.Response.PostReq;

@Service
public class PostService {

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	PostRepository postRepository;

	public String addPost(PostReq postReq, String userName, String coverImageUrl) {
		if (postReq.getTitle() == null || postReq.getTitle().isEmpty() ||
				postReq.getContent() == null || postReq.getContent().isEmpty()) {
			return "All required fields must be filled.";
		}
		try {
			Admin author = adminRepository.findByUserName(userName).orElse(null);
			Post post = new Post();
			post.setTitle(postReq.getTitle());
			post.setContent(postReq.getContent());
			post.setCreatedDate(new Date());
			post.setAuthor(author);
			post.setStatus(null);
			post.setCoverImageUrl(coverImageUrl);
			postRepository.save(post);
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error while creating post.";
		}
	}

	public List<Post> getPostsByUser(String userName) {
		Admin author = adminRepository.findByUserName(userName).orElse(null);
		if (author == null) {
			return Collections.emptyList();
		}
		return postRepository.findByAuthor(author);
	}

	public Post getPostById(Long postId) {
		return postRepository.findById(postId).orElse(null);
	}

	public void deletePost(Long postId) {
		postRepository.deleteById(postId);
	}

	public Page<Post> getAllPosts(Pageable pageable) {
		return postRepository.findAllByOrderByCreatedDateDesc(pageable);
	}

	public Page<Post> getPostsByAuthor(Admin currentUser, Pageable pageable) {
		return postRepository.findByAuthorOrderByCreatedDateDesc(currentUser, pageable);
	}
}
