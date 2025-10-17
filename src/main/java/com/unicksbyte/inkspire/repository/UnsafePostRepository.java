package com.unicksbyte.inkspire.repository;

import com.unicksbyte.inkspire.entity.UnsafePostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnsafePostRepository extends JpaRepository<UnsafePostEntity, Long> {


        List<UnsafePostEntity> findAllByOrderByFlaggedAtDesc();




}
