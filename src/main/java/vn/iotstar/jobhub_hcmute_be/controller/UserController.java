package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.dto.EmailVerificationRequest;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.UserProfileResponse;
import vn.iotstar.jobhub_hcmute_be.dto.VerifyOtpRequest;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name="User", description="User API")
public class UserController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/profile")
    @Operation(security = {@SecurityRequirement(name = "api-key")}) // Yêu cầu xác thực bằng API key
    //@SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GenericResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("Unauthorized: Empty or invalid token")
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .build()
                    );
        }
        try {
            UserDetail userDetail = (UserDetail) authentication.getPrincipal();

            return ResponseEntity.ok(
                    GenericResponse.builder()
                            .success(true)
                            .message("Retrieving user profile successfully")
                            .result(new UserProfileResponse(userDetail.getUser()))
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );
        } catch (ClassCastException e) {
            throw new UsernameNotFoundException("Invalid user principal type", e);
        }
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        boolean isOtpVerified = emailVerificationService.verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
        if (isOtpVerified) {
            return ResponseEntity.ok()
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("OTP verified successfully!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.badRequest()
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Invalid OTP or expired.")
                            .result(null)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
    }




}
