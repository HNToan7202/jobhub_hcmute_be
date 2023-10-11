package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.PasswordResetRequest;
import vn.iotstar.jobhub_hcmute_be.dto.UserProfileResponse;
import vn.iotstar.jobhub_hcmute_be.dto.VerifyOtpRequest;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.StudentService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {

    final JwtTokenProvider jwtTokenProvider;

    final UserService userService;

    final CloudinaryService cloudinaryService;

    private final EmailVerificationService emailVerificationService;

    private final UserRepository userRepository;

    final StudentService studentService;

    public UserController(JwtTokenProvider jwtTokenProvider, UserService userService, CloudinaryService cloudinaryService, EmailVerificationService emailVerificationService, UserRepository userRepository, StudentService studentService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.emailVerificationService = emailVerificationService;
        this.userRepository = userRepository;
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    @Operation(security = {@SecurityRequirement(name = "api-key")}) // Yêu cầu xác thực bằng API key
    public ResponseEntity<GenericResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GenericResponse.builder().success(false).message("Unauthorized: Empty or invalid token").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
        }
        try {
            UserDetail userDetail = (UserDetail) authentication.getPrincipal();

            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully").result(new UserProfileResponse(userDetail.getUser())).statusCode(HttpStatus.OK.value()).build());
        } catch (ClassCastException e) {
            throw new UsernameNotFoundException("Invalid user principal type", e);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        boolean isOtpVerified = emailVerificationService.verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
        if (isOtpVerified) {
            return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("OTP verified successfully!").result(null).statusCode(HttpStatus.OK.value()).build());
        } else if (userRepository.findByEmailAndIsActiveIsTrue(verifyOtpRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(true).message("Account is already verified!").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(true).message("Account is not found!").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GenericResponse.builder().success(false).message("Unauthorized: Empty or invalid token").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
        }
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return userService.changeUserPassord(userDetail.getUser(), passwordResetRequest);
    }
}
