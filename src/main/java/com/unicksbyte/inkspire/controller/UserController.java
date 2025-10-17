package com.unicksbyte.inkspire.controller;


import com.unicksbyte.inkspire.dto.EmailRequest;
import com.unicksbyte.inkspire.dto.ResetPasswordRequest;
import com.unicksbyte.inkspire.dto.UserRegistrationRequest;
import com.unicksbyte.inkspire.dto.UserResponse;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.register(request);

        if ("FAILED".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/verify-email")
    public UserResponse validateEmail(@RequestBody EmailRequest emailRequest){
        return userService.verifyEmail(emailRequest);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody EmailRequest emailRequest) {
        userService.resendOtp(emailRequest.getEmail());
        return ResponseEntity.ok("OTP has been resent to your email.");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest emailRequest) {
        userService.generatePasswordResetToken(emailRequest.getEmail());
        return ResponseEntity.ok("Password reset token sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password has been successfully updated.");
    }



}
