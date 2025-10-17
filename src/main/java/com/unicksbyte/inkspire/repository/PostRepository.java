package com.unicksbyte.inkspire.repository;

import com.unicksbyte.inkspire.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findByCategory(String category);

    List<PostEntity> findByUser_PublicIdOrderByCreatedAtDesc(String userPublicId);

    // Find by UUID publicId
    Optional<PostEntity> findByPublicId(String publicId);

    @Query(value = """
    SELECT * FROM posts p
    WHERE (:query IS NULL OR LOWER(p.title) LIKE CONCAT('%', LOWER(:query), '%') OR LOWER(p.content) LIKE CONCAT('%', LOWER(:query), '%'))
      AND (:category IS NULL OR p.category = :category)
""", nativeQuery = true)
    List<PostEntity> searchPostsNative(@Param("query") String query,
                                       @Param("category") String category);

}
