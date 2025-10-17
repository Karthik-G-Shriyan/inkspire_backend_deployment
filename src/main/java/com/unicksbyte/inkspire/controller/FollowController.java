package com.unicksbyte.inkspire.controller;

import com.unicksbyte.inkspire.dto.FollowResponse;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.service.FollowService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@AllArgsConstructor
public class FollowController {

    private final FollowService followService;


    @PostMapping("/{targetUserId}")
    public ResponseEntity<String> follow(@PathVariable String targetUserId) {
        followService.follow(targetUserId);
        return ResponseEntity.ok("Followed successfully");
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<String> unfollow(@PathVariable String targetUserId) {

        followService.unfollow(targetUserId);
        return ResponseEntity.ok("Unfollowed successfully");
    }

    @GetMapping("/{userId}/followers")
    public List<FollowResponse> getFollowers(@PathVariable String userId) {
        return followService.getFollowers(userId);
    }

    @GetMapping("/{userId}/following")
    public List<FollowResponse> getFollowing(@PathVariable String userId) {
        return followService.getFollowing(userId);
    }
}
