package com.example.springRest.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	// Used by PostService.getPostsByUser (non-paginated)
	List<Post> findByAuthor(Admin author);

	// Paginated feed queries
	Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);
	Page<Post> findByAuthorOrderByCreatedDateDesc(Admin author, Pageable pageable);
	Page<Post> findAllByStatus(String status, Pageable pageable);
	Page<Post> findByStatusAndAuthorNot(String status, Admin author, Pageable pageable);

	// Profile stats
	long countByAuthor(Admin author);
	Page<Post> findByAuthorAndStatusOrderByCreatedDateDesc(Admin author, String status, Pageable pageable);

	// Admin dashboard (no pagination needed)
	List<Post> findTop5ByOrderByCreatedDateDesc();

	@Query("SELECT p FROM Post p WHERE p.status IS NULL OR p.status = 'Pending' ORDER BY p.createdDate DESC")
	List<Post> findPendingPosts();

	@Query("SELECT COUNT(p) FROM Post p WHERE p.status IS NULL OR p.status = 'Pending'")
	long countPendingPosts();

	long countByStatus(String status);

	// Search — paginated
	@Query("SELECT p FROM Post p WHERE " +
		   "LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.author.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.author.userName) LIKE LOWER(CONCAT('%', :q, '%'))")
	Page<Post> searchAllPosts(@Param("q") String query, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.author = :author AND (" +
		   "LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')))")
	Page<Post> searchPostsByAuthor(@Param("q") String query, @Param("author") Admin author, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.status = 'Approved' AND (" +
		   "LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.author.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.author.userName) LIKE LOWER(CONCAT('%', :q, '%')))")
	Page<Post> searchApprovedPosts(@Param("q") String query, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.status = 'Approved' AND p.author <> :author AND (" +
		   "LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.author.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
		   "LOWER(p.author.userName) LIKE LOWER(CONCAT('%', :q, '%')))")
	Page<Post> searchApprovedPostsExcludingAuthor(@Param("q") String query, @Param("author") Admin author, Pageable pageable);
}
