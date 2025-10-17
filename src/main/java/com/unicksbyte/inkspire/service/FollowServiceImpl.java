package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.FollowResponse;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;

    private final UserService userService;


    @Override
    public void follow(String followingId) {

        // Get the currently logged-in user
        UserEntity follower = userService.findByUserId();

        if (Objects.equals(follower.getPublicId(), followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        // Get the user to follow
        UserEntity following = userRepository.findByPublicId(followingId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        follower.getFollowing().add(following);
        userRepository.save(follower);
    }


    @Override
    public void unfollow(String followingId) {

        // Get the currently logged-in user
        UserEntity follower = userService.findByUserId();

        // Get the user to unfollow
        UserEntity followingUser = userRepository.findByPublicId(followingId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        // Check if the user is actually being followed
        if (!follower.getFollowing().contains(followingUser)) {
            throw new IllegalStateException("You are not following this user");
        }

        follower.getFollowing().remove(followingUser);

        userRepository.save(follower);


    }


    @Override
    public List<FollowResponse> getFollowers(String userId) {

        // Get the user
        UserEntity user = userService.findByPublicId(userId);

        // Convert Set<UserEntity> to List<FollowResponse>
        return user.getFollowers()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    @Override
    public List<FollowResponse> getFollowing(String userId) {

        // Get the  user
        UserEntity user = userService.findByPublicId(userId);

        return user.getFollowing()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }



    private FollowResponse convertToResponse(UserEntity user) {
        return FollowResponse.builder()
                .publicId(user.getPublicId())
                .userName(user.getUserName())
                .email(user.getEmail()) // optional
                .build();
    }







}
