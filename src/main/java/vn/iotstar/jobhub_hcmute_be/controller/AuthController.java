package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.*;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.exception.UserNotFoundException;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.RefreshTokenService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Authentication", description = "Auth API")
public class AuthController {

    final
    UserService userService;

    final
    AuthenticationManager authenticationManager;

    final
    JwtTokenProvider jwtTokenProvider;

    final
    RefreshTokenService refreshTokenService;

    final
    PasswordEncoder passwordEncoder;

    final
    RoleRepository roleRepository;

    private final EmailVerificationService emailVerificationService;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    Environment env;

    @Autowired
    ResponseBuild responseBuild;


    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder, RoleRepository roleRepository, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Xử lý login ở đây
        return userService.userLogin(loginDTO);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        return refreshTokenService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(accessToken);
        if (userId != null) {
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

    @PostMapping("/forgot-password")
    public ResponseModel resetPassword(@RequestBody final ForgotPasswordto dto) throws MessagingException, UnsupportedEncodingException {
        ActionResult actionResult = new ActionResult();
        Optional<User> optionalUser = userService.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            actionResult.setErrorCode(USER_NOT_FOUND);
        }
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String otp = UUID.randomUUID().toString();
            userService.createPasswordResetOtpForUser(user, otp);
            String url = "https://job-hub-hcmute.vercel.app/forget-password/confirm-password?token=" + otp + "&email=" + user.getEmail();
            String subject = "Change Password For JobPost";
            Context context = new Context();
            context.setVariable("url", url);
            String content = templateEngine.process("forgot-password", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setTo(user.getEmail());
            helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")), "Recruiment Manager");
            javaMailSender.send(message);
            actionResult.setErrorCode(CHECK_MAIL_TO_RESET_PASSWORD);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/reset-password")
    public ResponseModel resetPassword(@RequestParam("token") String token, @Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        ActionResult actionResult = new ActionResult();
        String result = userService.validatePasswordResetOtp(token);
        if (result == null) {
            User user = userService.getUserByPasswordResetOtp(token)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found")).getUser();
            userService.changeUserPassword(user, passwordResetRequest.getNewPassword()
                    , passwordResetRequest.getConfirmPassword());
            actionResult.setErrorCode(RESET_PASSWORD_SUCCESS);

        } else {
            actionResult.setErrorCode(INTERNAL_SERVER_ERROR);

        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/confirm-password")
    public ResponseModel confirmPassword(@Valid @RequestBody ConfirmPassword confirmPassword) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.confirmPassword(confirmPassword);
        } catch (Exception e) {
            actionResult.setErrorCode(INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }

}
