package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.*;
import vn.iotstar.jobhub_hcmute_be.dto.EmailVerificationRequest;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.RefreshTokenService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name="Authentication", description="Auth API")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Xử lý login ở đây
        return userService.userLogin(loginDTO);
    }

//    @PostMapping("/refresh-access-token")
//    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
//        String refresh_Token = refreshToken.substring(7);
//        return refreshTokenService.refreshAccessToken(refresh_Token);
//    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        return refreshTokenService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(accessToken);
        if (userId!= null){
            return refreshTokenService.logout(userId);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Logout failed!")
                        .result("Please login before logout!")
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }


    @PostMapping("/create-admin")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody SignUpMailDTO signUpMailDTO) {
        Admin admin = new Admin();
        admin.setEmail(signUpMailDTO.getEmail());
        admin.setPassword(passwordEncoder.encode(signUpMailDTO.getPassword()));
        admin.setRole(roleRepository.findByName("ADMIN"));
        admin = userService.save(admin);
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Sign Up Admin Success!")
                        .result(admin)
                        .statusCode(200)
                        .build()
        );

    }

    @PostMapping("/signup-email")
    public ResponseEntity<GenericResponse> signUpMail(
            @RequestBody @Valid RegisterRequest signUpMailDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors() ) {
            String errorMessage = Objects.requireNonNull(
                    bindingResult.getFieldError()).getDefaultMessage();

            return ResponseEntity.internalServerError().body(
                    GenericResponse.builder()
                            .success(true)
                            .message(errorMessage)
                            .statusCode(200)
                            .build()
            );

        }
        return userService.userRegisterEmail(signUpMailDTO);
    }

    @PostMapping("/employer-signup-email")
    public ResponseEntity<?> employerSignUpMail(
            @RequestBody @Valid EmployerRegisterDTO employerRegisterDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(
                    bindingResult.getFieldError()).getDefaultMessage();

            return ResponseEntity.internalServerError().body(
                    GenericResponse.builder()
                            .success(true)
                            .message(errorMessage)
                            .statusCode(200)
                            .build()
            );

        }
        return userService.employerRegister(employerRegisterDTO);
    }
    @PostMapping("/send-otp")
    public ResponseEntity<GenericResponse> sendOtp(@RequestBody EmailVerificationRequest emailVerificationRequest) {
        try {
            emailVerificationService.sendOtp(emailVerificationRequest.getEmail());
            return ResponseEntity.ok()
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("OTP sent successfully!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("An error occurred while sending OTP.")
                            .result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

}
