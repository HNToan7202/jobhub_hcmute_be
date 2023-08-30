package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.SignUpMailDTO;
import vn.iotstar.jobhub_hcmute_be.dto.UserProfileResponse;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.exception.UserNotFoundException;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.repository.VerificationTokenRepository;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.RefreshTokenService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

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
    EmailVerificationService emailVerificationService;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    TemplateEngine templateEngine;


    @Override
    @Deprecated
    public User getById(String s) {
        return userRepository.getById(s);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
    }

    @Override
    public Optional<User> findById(String s) {
        return userRepository.findById(s);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void deleteById(String s) {
        userRepository.deleteById(s);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    @Override
    public List<User> findAll(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public ResponseEntity<GenericResponse> getProfile(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new RuntimeException("User not found");

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Retrieving user profile successfully")
                        .result(new UserProfileResponse(user.get()))
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public ResponseEntity<GenericResponse> userLogin(LoginDTO loginDTO) {

        if (findByEmail(loginDTO.getUserLogin()).isEmpty())
            throw new UserNotFoundException("Account does not exist");
        Optional<User> optionalUser = findByEmail(loginDTO.getUserLogin());
        if (optionalUser.isPresent() && !optionalUser.get().isVerified()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.builder()
                    .success(false)
                    .message("Your account is not verified!")
                    .result(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build());
        }

        //Optional<User> optionalUser = findByEmail(loginDTO.getUserLogin());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUserLogin(),
                        loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetail);
        RefreshToken refreshToken = new RefreshToken();
        String token = jwtTokenProvider.generateRefreshToken(userDetail);
        refreshToken.setToken(token);
        refreshToken.setUser(userDetail.getUser());
        //invalid all refreshToken before
        refreshTokenService.revokeRefreshToken(userDetail.getUserId());
        refreshTokenService.save(refreshToken);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", token);

        if (optionalUser.isPresent()) {
            optionalUser.get().setLastLoginAt(new Date());
            save(optionalUser.get());
        }

        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Login successfully!")
                .result(tokenMap)
                .statusCode(HttpStatus.OK.value())
                .build());

    }


    @Override
    public ResponseEntity<GenericResponse> userRegisterEmail(SignUpMailDTO signUpMailDTO) {
        if (signUpMailDTO.getPassword().length() < 8 || signUpMailDTO.getPassword().length() > 32)
            throw new RuntimeException("Password must be between 8 and 32 characters long");

        Optional<User> userOptional = findByEmail(signUpMailDTO.getEmail());
        if (userOptional.isPresent())
            return ResponseEntity.status(409)
                    .body(
                            GenericResponse.builder()
                                    .success(true)
                                    .message("Email already in use")
                                    .result(null)
                                    .statusCode(HttpStatus.CONFLICT.value())
                                    .build()
                    );



        Student user = new Student();
//        user.setFullName(registerRequest.getFullName());
        user.setEmail(signUpMailDTO.getEmail());
        //user.setPhone(registerRequest.getPhone());
        user.setUserId(UUID.randomUUID().toString().split("-")[0]);
        user.setPassword(passwordEncoder.encode(signUpMailDTO.getEmail()));
        user.setRole(roleRepository.findByName("STUDENT"));

        save(user);

        emailVerificationService.sendOtp(signUpMailDTO.getEmail());

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Sign Up Success")
                        .result(null)
                        .statusCode(200)
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> getAccounts(int size, int page) throws Exception {
        if (page < 0) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .message("Page index must not be less than 0")
                    .statusCode(500)
                    .success(false)
                    .build());
        }
        Pageable pageable = PageRequest.of(page,size);
        Page<User> users = findAll(pageable);


        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .message("Get user successful")
                        .result(users)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> validateVerificationAccount(String token) {

//        String message = "Invalid token, please check the token again!";
//        EmailVerification emailVerification = emailVerificationService.findByToken(token);
//        if (verificationToken != null) {
//            message = "Account verification successful, please login!";
//            Context context = new Context();
//            context.setVariable("result", message);
//            String content = templateEngine.process("result-confirm", context);
//            User user = verificationToken.getUser();
//            user.setVerified(true);
//            user = userRepository.save(user);
//        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .message("message")
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
