package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.EmailRequest;
import com.unicksbyte.inkspire.dto.ResetPasswordRequest;
import com.unicksbyte.inkspire.dto.UserRegistrationRequest;
import com.unicksbyte.inkspire.dto.UserResponse;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.exception.InvalidOtpException;
import com.unicksbyte.inkspire.exception.ResourceNotFoundException;
import com.unicksbyte.inkspire.exception.UnauthorizedActionException;
import com.unicksbyte.inkspire.repository.UserRepository;
import com.unicksbyte.inkspire.utils.OtpGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationFacade authenticationFacade;

    private final OtpGenerator otpGenerator;

    private final EmailService emailService;

    @Override
    public UserResponse register(UserRegistrationRequest request) {


        Optional<UserEntity> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            // Return null or a special response indicating already registered
            return UserResponse.builder()
                    .email(request.getEmail())
                    .message("User already registered with this email")
                    .status("FAILED") // optional field
                    .build();
        }

        UserEntity newUser = convertToEntity(request);



        //email verification
        String token = otpGenerator.generateOtp();
        newUser.setEmailVerificationToken(token);
        newUser.setTokenExpiry(LocalDateTime.now().plusMinutes(15)); // optional 15m expiry
        newUser.setEmailVerified(false);

        //save to DB
        newUser = userRepository.save(newUser);

        //send otp through mail
        String subject = "Your OTP for Inkspire Login";
        String body = "Hi,\\n\\nYour OTP is:"+ token +"\\n\\nThis OTP is valid for 10 minutes.\\n\\nThanks,\\nInkspire Team";
        emailService.sendEmail(request.getEmail(), subject, body);

        UserResponse response = convertToResponse(newUser);


      //  emailService.sendVerificationEmail(user.getEmail(), token);


        return response;
    }

    @Override
    public UserEntity findByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with publicId: " + publicId));
    }

    @Override
    public String findByUserEmail() {

        Authentication auth = authenticationFacade.getAuthentication();
        String loggedInUserEmail = auth.getName();

        UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("username not found..!"));
        return loggedInUser.getUserName();

    }

    @Override
    public UserEntity findByUserId() {
        Authentication auth = authenticationFacade.getAuthentication();
        String loggedInUserEmail = auth.getName();

        UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("username not found..!"));
        return loggedInUser;
    }

    @Override
    public UserResponse verifyEmail(EmailRequest emailRequest) {
        Optional<UserEntity> registeredUserOpt = userRepository.findByEmail(emailRequest.getEmail());

        if (registeredUserOpt.isEmpty()) {
            throw new ResourceNotFoundException("User with email " + emailRequest.getEmail() + " not found");
        }

        UserEntity registeredUser = registeredUserOpt.get();

        //  Check if OTP matches
        if (!registeredUser.getEmailVerificationToken().equals(emailRequest.getOtp())) {
            throw new InvalidOtpException("Invalid OTP provided");
        }

        //  Check if OTP has expired
        if (registeredUser.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("OTP has expired. Please request a new one.");
        }

        // 4⃣ Mark email as verified
        registeredUser.setEmailVerified(true);

        //  Clear OTP and expiry (optional but recommended)
        registeredUser.setEmailVerificationToken(null);
        registeredUser.setTokenExpiry(null);

        // Save changes
        userRepository.save(registeredUser);

        // Convert to response and return
        return convertToResponse(registeredUser);

    }

    @Override
    public void resendOtp(String email) {
        //  Find the user
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User with email " + email + " not found");
        }

        UserEntity user = userOpt.get();

        //  Check if user is already verified
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified.");
        }

        //  Generate new OTP and set expiry
        String newOtp = otpGenerator.generateOtp();
        user.setEmailVerificationToken(newOtp);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(10)); // for example, 10 min expiry

        // Save user entity
        userRepository.save(user);

        //  Send email with new OTP
        String subject = "Your new OTP for Inkspire Login";
        String body = "Hi,\\n\\nYour OTP is:"+ newOtp +"\\n\\nThis OTP is valid for 10 minutes.\\n\\nThanks,\\nInkspire Team";
        emailService.sendEmail(email, subject, body);
    }


    @Override
    public void generatePasswordResetToken(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User with email " + email + " not found");
        }

        UserEntity user = userOpt.get();

        // Generate a secure token (UUID or 6-digit OTP)
        String resetToken = otpGenerator.generateOtp();
        user.setEmailVerificationToken(resetToken);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(10)); // valid for 10 min

        userRepository.save(user);

        // Send email
        String subject = "Reset Your Password";
        String body = "Hi,\\n\\nYour OTP is:"+ resetToken +"\\n\\nThis OTP is valid for 10 minutes.\\n\\nThanks,\\nInkspire Team";
        emailService.sendEmail(email, subject, body);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User with email " + request.getEmail() + " not found");
        }

        UserEntity user = userOpt.get();

        // Validate token
        if (!request.getOtp().equals(user.getEmailVerificationToken())) {
            throw new UnauthorizedActionException("Invalid reset token");
        }

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedActionException("Reset password OTP has expired. try again");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Invalidate token
        user.setEmailVerificationToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);
    }



    private UserEntity convertToEntity(UserRegistrationRequest request) {
        return UserEntity.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
    }

    private UserResponse convertToResponse(UserEntity registeredUser) {
        return UserResponse.builder()
                .userName(registeredUser.getUserName())
                .email(registeredUser.getEmail())
                .publicId(registeredUser.getPublicId())
                .build();
    }

}
