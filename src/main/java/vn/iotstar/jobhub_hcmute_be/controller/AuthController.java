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
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.SignUpMailDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
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


    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {

        //Xử lý login ở đây
        return userService.userLogin(loginDTO);

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    @RequestParam("refreshToken") String refreshToken) {
        String accessToken = authorizationHeader.substring(7);
        if (jwtTokenProvider.getUserIdFromJwt(accessToken).equals(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken))) {
            return refreshTokenService.logout(refreshToken);
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
            @RequestBody @Valid SignUpMailDTO signUpMailDTO,
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
        return userService.userRegisterEmail(signUpMailDTO);
    }

}
