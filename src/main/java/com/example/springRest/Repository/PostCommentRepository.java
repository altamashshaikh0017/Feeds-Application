package com.example.springRest.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;
import com.example.springRest.Model.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

	List<PostComment> findByPostOrderByCommentedAtDesc(Post post);

	long countByPost(Post post);

	@org.springframework.data.jpa.repository.Query(
		"SELECT COUNT(c) FROM PostComment c WHERE c.post.author = :author")
	long countCommentsReceivedByAuthor(
		@org.springframework.data.repository.query.Param("author") Admin author);
}
