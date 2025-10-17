package com.unicksbyte.inkspire.repository;

import com.unicksbyte.inkspire.entity.CommentEntity;
import com.unicksbyte.inkspire.entity.PostEntity;
import com.unicksbyte.inkspire.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPost(PostEntity post);

    List<CommentEntity> findByUser(UserEntity user);

    Optional<CommentEntity> findByPublicId(String publicId);
}
