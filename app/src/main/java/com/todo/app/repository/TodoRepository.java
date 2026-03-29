package com.todo.app.repository;

import com.todo.app.model.Priority;
import com.todo.app.model.Todo;
import com.todo.app.model.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

    // ── Basic ownership check ─────────────────────────────────────────────────
    Optional<Todo> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    // ── Simple filters ────────────────────────────────────────────────────────
    List<Todo> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<Todo> findAllByUserIdAndStatus(Long userId, TodoStatus status);

    List<Todo> findAllByUserIdAndPriority(Long userId, Priority priority);

    // ── Statistics ────────────────────────────────────────────────────────────
    long countByUserIdAndStatus(Long userId, TodoStatus status);

    // ── Tag search ────────────────────────────────────────────────────────────
    @Query("""
            SELECT DISTINCT t FROM Todo t
            JOIN t.tags tag
            WHERE t.user.id = :userId
            AND LOWER(tag) = LOWER(:tag)
            ORDER BY t.createdAt DESC
            """)
    List<Todo> findAllByUserIdAndTag(@Param("userId") Long userId, @Param("tag") String tag);

    // ── Full-text search across title, description, notes ─────────────────────
    @Query("""
            SELECT t FROM Todo t
            WHERE t.user.id = :userId
            AND (
                LOWER(t.title)       LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(t.notes)    LIKE LOWER(CONCAT('%', :q, '%'))
            )
            ORDER BY t.createdAt DESC
            """)
    List<Todo> searchByKeyword(@Param("userId") Long userId, @Param("q") String q);

    // ── Get all distinct tags for a user (optimized query) ────────────────────
    @Query("""
            SELECT DISTINCT tag FROM Todo t
            JOIN t.tags tag
            WHERE t.user.id = :userId
            ORDER BY tag ASC
            """)
    List<String> getAllDistinctTags(@Param("userId") Long userId);
}