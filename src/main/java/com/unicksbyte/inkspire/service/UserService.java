package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.EmailRequest;
import com.unicksbyte.inkspire.dto.ResetPasswordRequest;
import com.unicksbyte.inkspire.dto.UserRegistrationRequest;
import com.unicksbyte.inkspire.dto.UserResponse;
import com.unicksbyte.inkspire.entity.UserEntity;

public interface UserService {

    UserResponse register(UserRegistrationRequest request);

    String findByUserEmail();

    UserEntity findByPublicId(String publicId);

    UserEntity findByUserId();

    UserResponse verifyEmail(EmailRequest emailRequest);

    void resendOtp(String email);

    void generatePasswordResetToken(String email);

    void resetPassword(ResetPasswordRequest request);


//    UserResponse getProfile(Long userId);
}
