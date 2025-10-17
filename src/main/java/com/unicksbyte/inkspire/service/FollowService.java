package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.FollowResponse;
import com.unicksbyte.inkspire.entity.UserEntity;

import java.util.List;
import java.util.Set;

public interface FollowService {

    void follow(String followingId);

    void unfollow(String followingId);

    List<FollowResponse> getFollowers(String userId);

    List<FollowResponse> getFollowing(String userId);
}
