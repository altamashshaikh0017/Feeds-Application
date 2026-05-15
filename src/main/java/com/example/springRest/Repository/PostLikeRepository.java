package com.example.springRest.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;
import com.example.springRest.Model.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	Optional<PostLike> findByPostAndLikedBy(Post post, Admin admin);

	List<PostLike> findByPostOrderByLikedAtDesc(Post post);

	long countByPost(Post post);

	boolean existsByPostAndLikedBy(Post post, Admin admin);

	@org.springframework.data.jpa.repository.Query(
		"SELECT COUNT(l) FROM PostLike l WHERE l.post.author = :author")
	long countLikesReceivedByAuthor(
		@org.springframework.data.repository.query.Param("author") Admin author);
}
