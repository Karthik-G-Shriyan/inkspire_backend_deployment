package com.unicksbyte.inkspire.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "unsafe_posts")
public class UnsafePostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private String publicId;

    // Each unsafe post belongs to exactly one post (1:1)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private PostEntity post;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(length = 100)
    private String keywords;

    private LocalDateTime flaggedAt;

    @PrePersist
    public void prePersist() {
        this.publicId = UUID.randomUUID().toString();
        this.flaggedAt = LocalDateTime.now();
    }
}
