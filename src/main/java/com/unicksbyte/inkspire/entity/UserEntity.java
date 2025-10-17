package com.unicksbyte.inkspire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column( unique = true , updatable = false)
    private String publicId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;

    //for email verification and password reset
    @Column(nullable = false)
    private boolean isEmailVerified ;

    private String emailVerificationToken;

    private LocalDateTime tokenExpiry;

    // ✅ Users I follow
    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"),         // current user
            inverseJoinColumns = @JoinColumn(name = "following_id")  // user they follow
    )
    private Set<UserEntity> following = new HashSet<>();

    // ✅ Users who follow me
    @ManyToMany(mappedBy = "following")
    private Set<UserEntity> followers = new HashSet<>();



    // Auto-generate UUID before saving
    @PrePersist
    public void generatePublicId() {
        this.publicId = UUID.randomUUID().toString();
    }


}
