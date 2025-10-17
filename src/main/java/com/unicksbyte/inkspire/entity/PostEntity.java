package com.unicksbyte.inkspire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , updatable = false)
    private String publicId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false , columnDefinition = "VARCHAR(MAX)")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // foreign key in posts table
    private UserEntity user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
    private String category;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private UnsafePostEntity unsafePost;


    // Auto-generate UUID before saving
    @PrePersist
    public void generatePublicId() {
        this.publicId = UUID.randomUUID().toString();
    }
}
