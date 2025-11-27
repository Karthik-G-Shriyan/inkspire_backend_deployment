package com.unicksbyte.inkspire.repository;

import com.unicksbyte.inkspire.entity.PostEntity;
import com.unicksbyte.inkspire.projection.PostPreviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @Query("""
    SELECT p.publicId AS publicId,
           p.title AS title,
           p.preview AS preview,
           u.publicId AS authorId,
           u.userName AS authorName,
           p.category AS category,
           p.updatedAt AS updatedAt,
           p.tags AS tags
    FROM PostEntity p
    JOIN p.user u
    ORDER BY p.createdAt DESC
""")
    Page<PostPreviewProjection> findAllPostPreviews(Pageable pageable);


    @Query(value = """
    SELECT * FROM posts p
    WHERE (:query IS NULL OR LOWER(p.title) LIKE CONCAT('%', LOWER(:query), '%') OR LOWER(p.content) LIKE CONCAT('%', LOWER(:query), '%'))
      AND (:category IS NULL OR p.category = :category)
""", nativeQuery = true)
    List<PostEntity> searchPostsNative(@Param("query") String query,
                                       @Param("category") String category);

}
