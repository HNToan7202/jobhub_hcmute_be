package vn.iotstar.jobhub_hcmute_be.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;
import vn.iotstar.jobhub_hcmute_be.constant.Utils;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.EmployerRegisterDTO;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.LoginReq;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.RegisterRequest;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.exception.UserNotFoundException;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.*;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;

import vn.iotstar.jobhub_hcmute_be.service.*;
import vn.iotstar.jobhub_hcmute_be.utils.Constants;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.RefreshTokenService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;


import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl extends RedisServiceImpl implements UserService {
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

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    PasswordResetOtpRepository passwordResetOtpRepository;

    @Autowired
    private StudentRepository studentRepository;


    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private EmployerRepository employerRepository;

    ObjectMapper objectMapper;

    public UserServiceImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate);
        this.objectMapper = objectMapper;
    }


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


    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private Environment env;


    @Override
    public ActionResult getProfile(String userId) throws JsonProcessingException {
        ActionResult actionResult = new ActionResult();
        objectMapper = new ObjectMapper();
//        if (this.hashExists(Constants.USERS, userId)) {
//            Object jobs = this.hashGet(Constants.USERS, userId);
//            HashMap<String, Object> data = objectMapper.readValue(jobs.toString(), HashMap.class);
//            data.put("dateOfBirth", new Date((Long) data.get("dateOfBirth")));
//            actionResult.setErrorCode(ErrorCodeEnum.REDIS_GET_SUCCESS);
//            actionResult.setData(data);
//            return actionResult;
//        }
        Optional<Student> student = studentRepository.findById(userId);
        if (student.isEmpty())
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        else {
            ProfileDTO profileDTO = new ProfileDTO();
            BeanUtils.copyProperties(student.get(), profileDTO);
            actionResult.setData(profileDTO);
//            this.hashSet(Constants.USERS, userId, objectMapper.writeValueAsString(profileDTO));
            actionResult.setErrorCode(ErrorCodeEnum.GET_PROFILE_SUCCESSFULLY);
        }
        return actionResult;
    }

    @Override
    public ActionResult getProfileAdmin(String userId) throws JsonProcessingException {
        ActionResult actionResult = new ActionResult();
        Optional<Admin> student = adminRepository.findById(userId);
        if (student.isEmpty())
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        else {
            objectMapper = new ObjectMapper();
            if (this.hashExists(Constants.USERS, userId)) {
                Object jobs = this.hashGet(Constants.USERS, userId);
                HashMap<String, Object> data = objectMapper.readValue(jobs.toString(), HashMap.class);
                actionResult.setErrorCode(ErrorCodeEnum.REDIS_GET_SUCCESS);
                actionResult.setData(data);
                return actionResult;
            }
            ProfileDTO profileDTO = new ProfileDTO();
            BeanUtils.copyProperties(student.get(), profileDTO);
            actionResult.setData(profileDTO);
            this.hashSet(Constants.USERS, userId, objectMapper.writeValueAsString(profileDTO));
            actionResult.setErrorCode(ErrorCodeEnum.GET_ADMIN_PROFILE_SUCCESSFULLY);
        }
        return actionResult;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Lưu thông tin xác thực của người dùng vào Redis
    public void saveUserDetailToRedis(UserDetail userDetails, String accessToken, Object credentials, Object principal) {
        this.saveSession(accessToken, userDetails, credentials, principal);
    }

    @Override
    public ResponseEntity<GenericResponse> userLogin(LoginDTO loginDTO) {

        if (findByEmail(loginDTO.getUserLogin()).isEmpty())
            throw new UserNotFoundException("Account does not exist");
        Optional<User> optionalUser = findByEmail(loginDTO.getUserLogin());
//        optionalUser.get().setPassword(passwordEncoder.encode("28072002Thanh@"));
//        userRepository.save(optionalUser.get());
        if (optionalUser.isPresent() && !optionalUser.get().isVerified()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.builder()
                    .success(false)
                    .message("Your account is not verified!")
                    .result(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build());
        }

        if (optionalUser.isPresent() && !optionalUser.get().getIsActive()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.builder()
                    .success(false)
                    .message("Your has been blocked!")
                    .result(null)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }

        //Optional<User> optionalUser = findByEmail(loginDTO.getUserLogin());
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginDTO.getUserLogin(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetail);
        RefreshToken refreshToken = new RefreshToken();
        String token = jwtTokenProvider.generateRefreshToken(userDetail);
        refreshToken.setToken(token);
        refreshToken.setUser(userDetail.getUser());
        if (this.exists(userDetail.getUser().getUserId())) {
            this.deleteSession(userDetail.getUser().getUserId());
        }
        saveUserDetailToRedis(userDetail, userDetail.getUser().getUserId(), authRequest.getCredentials(), authRequest.getPrincipal());
        //invalid all refreshToken before
        refreshTokenService.revokeRefreshToken(userDetail.getUserId());
        refreshTokenService.save(refreshToken);
        String username = CurrentUserUtils.getCurrentUserName();
        String userId = CurrentUserUtils.getCurrentUserId();


        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", token);
        tokenMap.put("role", userDetail.getUser().getRole().getName());

        tokenMap.put("username", username);
        tokenMap.put("userId", userId);

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
    public ActionResult login(LoginReq loginDTO) {
        ActionResult actionResult = new ActionResult();
        if (findByEmail(loginDTO.getUserLogin()).isEmpty())
            throw new UserNotFoundException("Account does not exist");
        Optional<User> optionalUser = findByEmail(loginDTO.getUserLogin());
//        optionalUser.get().setPassword(passwordEncoder.encode("28072002Thanh@"));
//        userRepository.save(optionalUser.get());
        if (optionalUser.isPresent() && !optionalUser.get().isVerified()) {

            actionResult.setErrorCode(ErrorCodeEnum.ACCOUNT_NOT_VERIFIED);
        }

        if (optionalUser.isPresent() && !optionalUser.get().getIsActive()) {
            actionResult.setErrorCode(ErrorCodeEnum.ACCOUNT_NOT_ACTIVE);
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
        tokenMap.put("role", userDetail.getUser().getRole().getName());

        if (optionalUser.isPresent()) {
            optionalUser.get().setLastLoginAt(new Date());
            save(optionalUser.get());
        }

        actionResult.setData(tokenMap);
        actionResult.setErrorCode(ErrorCodeEnum.LOGIN_SUCCESSFULLY);
        return actionResult;


    }


    @Override
    public ResponseEntity<GenericResponse> changeUserPassord(User user, PasswordResetRequest request) {
        String oldPass = user.getPassword();
        String oldPassDTO = passwordEncoder.encode(request.getOldPassword());
        if (oldPassDTO.equals(oldPassDTO)) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user = save(user);
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Change Password successfully!")
                    //.result(user)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
        return ResponseEntity.status(400)
                .body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Old Password don't match")
                                .result(null)
                                .statusCode(HttpStatus.CONFLICT.value())
                                .build()
                );

    }


    @Override
    public ResponseEntity<?> employerRegister(EmployerRegisterDTO employerRegisterDTO) {
        if (employerRegisterDTO.getPassword().length() < 8 || employerRegisterDTO.getPassword().length() > 32)
            throw new RuntimeException("Password must be between 8 and 32 characters long");

        Optional<User> userOptional = findByEmail(employerRegisterDTO.getEmail());
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
        if (!employerRegisterDTO.getPassword().equals(employerRegisterDTO.getConfirmPassword()))
            return ResponseEntity.status(409)
                    .body(
                            GenericResponse.builder()
                                    .success(true)
                                    .message("Password and confirm password do not match")
                                    .result(null)
                                    .statusCode(HttpStatus.CONFLICT.value())
                                    .build()
                    );

        Employer user = new Employer();
        user.setCompanyName(employerRegisterDTO.getFullName());
        user.setEmail(employerRegisterDTO.getEmail());
        user.setUserId(UUID.randomUUID().toString().split("-")[0]);
        user.setPassword(passwordEncoder.encode(employerRegisterDTO.getPassword()));
        user.setRole(roleRepository.findByName("EMPLOYER"));

        user.setEmployState(EmployState.PENDDING);
        user = save(user);
        if (this.exists(Constants.EMPLOYERS)) this.delete(Constants.EMPLOYERS);
        emailVerificationService.sendOtpEmployer(employerRegisterDTO.getEmail(), employerRegisterDTO.getFullName());

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Sign Up Success. Please check your email to verify your account!")
                        .result(user)
                        .statusCode(200)
                        .build()
        );
    }


    @Override
    public ResponseEntity<GenericResponse> userRegisterEmail(RegisterRequest registerRequest) {
        if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
            throw new RuntimeException("Password must be between 8 and 32 characters long");

        Optional<User> userOptional = findByEmail(registerRequest.getEmail());
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

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
            return ResponseEntity.status(409)
                    .body(
                            GenericResponse.builder()
                                    .success(true)
                                    .message("Password and confirm password do not match")
                                    .result(null)
                                    .statusCode(HttpStatus.CONFLICT.value())
                                    .build()
                    );

        Student user = new Student();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setUserId(UUID.randomUUID().toString().split("-")[0]);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(roleRepository.findByName("STUDENT"));
        Resume resume = new Resume();
        resume.setStudent(user);
        user.setResume(resume);
        user = save(user);
        emailVerificationService.sendOtp(user.getEmail());

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Sign Up Success. Please check your email to verify your account!")
                        .result(user)
                        .statusCode(200)
                        .build()
        );
    }


    @Override
    public ResponseEntity<GenericResponse> getAccounts(int size, int page) {
        if (page < 0) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .message("Page index must not be less than 0")
                    .statusCode(500)
                    .success(false)
                    .build());
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = findAll(pageable);

        return getGenericResponseResponseEntity(users);
    }

    @NotNull
    private ResponseEntity<GenericResponse> getGenericResponseResponseEntity(Page<User> users) {
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("currentPage", users.getNumber());
        response.put("totalItems", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .message("Get user successful")
                        .result(response)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }


    @Override
    public ActionResult getAccounts(int size, int page, String role, Boolean isActive) throws Exception {
        ActionResult actionResult = new ActionResult();
        String field = String.valueOf(page) + String.valueOf(size) + role + String.valueOf(isActive);
        if (this.hashExists(Constants.USERS, field)) {
            Object jobs = this.hashGet(Constants.USERS, field);
            HashMap<String, Object> data = objectMapper.readValue(jobs.toString(), HashMap.class);
            actionResult.setErrorCode(ErrorCodeEnum.REDIS_GET_SUCCESS);
            actionResult.setData(data);
            return actionResult;
        }
        if (page < 0) {
            actionResult.setErrorCode(ErrorCodeEnum.PAGE_INDEX_MUST_NOT_BE_LESS_THAN_0);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;

        switch (role) {
            case "ALL" -> {

                if (isActive == null) {
                    users = findAll(pageable);
                } else {
                    users = userRepository.findByIsActive(isActive, pageable);
                }
                Map<String, Object> response = toMap(users);
                actionResult.setData(response);
                actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_USER_SUCCESSFULLY);
                this.hashSet(Constants.USERS, field, objectMapper.writeValueAsString(response));
            }
            case "STUDENT", "EMPLOYER", "ADMIN" -> {
                if (isActive == null) {
                    users = userRepository.findByRole_Name(role, pageable);
                } else {
                    users = userRepository.findByRole_NameAndIsActive(role, isActive, pageable);
                }

                Map<String, Object> response = toMap(users);
                actionResult.setData(response);
                actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_USER_SUCCESSFULLY);
                this.hashSet(Constants.USERS, field, objectMapper.writeValueAsString(response));
            }
            default -> throw new Exception("Invalid role");
        }
        return actionResult;
    }

    private Map<String, Object> toMap(Page<User> users) {
        Map<String, Object> response = new HashMap<>();
        response.put("user", users.getContent());
        response.put("pageNumber", users.getNumber());
        response.put("pageSize", users.getSize());
        response.put("totalPages", users.getTotalPages());
        response.put("totalElements", users.getTotalElements());
        return response;
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

    @Override
    public void createPasswordResetOtpForUser(User user, String otp) {
        PasswordResetOtp myOtp = null;

        Optional<PasswordResetOtp> optionalUser = passwordResetOtpRepository.findByUser(user);
        if (optionalUser.isPresent()) {
            myOtp = passwordResetOtpRepository.findByUser(user).get();
            myOtp.updateOtp(otp);
        } else {
            myOtp = new PasswordResetOtp(otp, user);
        }
        passwordResetOtpRepository.save(myOtp);
    }


    @Override
    public String validatePasswordResetOtp(String otp) {

        Optional<PasswordResetOtp> passOtp = passwordResetOtpRepository.findByOtp(otp);
        Calendar cal = Calendar.getInstance();

        if (passOtp.isEmpty()) {
            return "Invalid token/link";
        }
        if (passOtp.get().getExpiryDate().before(cal.getTime())) {
            return "Token/link expired";
        }
        return null;

    }

    public String validatePasswordReset(String otp, String mail) {

        Optional<PasswordResetOtp> passOtp = passwordResetOtpRepository.findByOtp(otp);
        Calendar cal = Calendar.getInstance();

        if (passOtp.isEmpty() || !passOtp.get().getUser().getEmail().equals(mail)) {
            return "Invalid token/link";
        }
        if (passOtp.get().getExpiryDate().before(cal.getTime()) || !passOtp.get().getUser().getEmail().equals(mail)) {
            return "Token/link expired";
        }
        return null;

    }

    @Override
    public Optional<PasswordResetOtp> getUserByPasswordResetOtp(String otp) {
        return passwordResetOtpRepository.findByOtp(otp);
    }

    @Override
    public void changeUserPassword(User user, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword))
            throw new RuntimeException("Password and confirm password do not match");
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
    }

    @Override
    public ActionResult getAllEmployer(Pageable pageable, String companyName, String address, String teamSize) throws JsonProcessingException {
        ActionResult actionResult = new ActionResult();
        String field = pageable.getPageNumber() + pageable.getPageSize() + companyName + teamSize;
        if (this.hashExists(Constants.EMPLOYERS, field)) {
            Object jobs = this.hashGet(Constants.EMPLOYERS, field);
            HashMap<String, Object> data = objectMapper.readValue(jobs.toString(), HashMap.class);
            actionResult.setErrorCode(ErrorCodeEnum.REDIS_GET_SUCCESS);
            actionResult.setData(data);
            return actionResult;
        }
        Page<Employer> employers = employerRepository.findEmployers(pageable, companyName, teamSize);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("employers", employers.getContent());
        map.put("pageNumber", employers.getPageable().getPageNumber());
        map.put("pageSize", employers.getSize());
        map.put("totalPages", employers.getTotalPages());
        map.put("totalElements", employers.getTotalElements());
        this.hashSet(Constants.EMPLOYERS, field, objectMapper.writeValueAsString(map));
        actionResult.setData(map);
        actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_EMPLOYER_SUCCESSFULLY);
        return actionResult;
    }

    @Override
    public ActionResult detailProfileEmployer(String employerId) throws JsonProcessingException {
        ActionResult actionResult = new ActionResult();
        objectMapper = new ObjectMapper();
        if (this.hashExists(Constants.EMPLOYERS, employerId)) {
            Object jobs = this.hashGet(Constants.EMPLOYERS, employerId);
            HashMap<String, Object> data = objectMapper.readValue(jobs.toString(), HashMap.class);
            actionResult.setErrorCode(ErrorCodeEnum.REDIS_GET_SUCCESS);
            actionResult.setData(data);
            return actionResult;
        }
        Optional<Employer> employer = employerRepository.findById(employerId);
        if (employer.isEmpty()) {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        } else {

            EmployerDTO employerDTO = new EmployerDTO();
            BeanUtils.copyProperties(employer.get(), employerDTO);
            this.hashSet(Constants.EMPLOYERS, employerId, objectMapper.writeValueAsString(employerDTO));
            actionResult.setData(employerDTO);
            actionResult.setErrorCode(ErrorCodeEnum.GET_PROFILE_EMPLOYER_SUCCESSFULLY);
        }
        return actionResult;
    }

    @Override
    public ActionResult confirmPassword(ConfirmPassword confirmPassword) {
        ActionResult actionResult = new ActionResult();
        Optional<User> user = userRepository.findByEmail(confirmPassword.getEmail());
        if (user.isEmpty()) {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        } else {
            String validate = validatePasswordReset(confirmPassword.getOtp(), confirmPassword.getEmail());
            if (validate != null) {
                actionResult.setErrorCode(ErrorCodeEnum.INVALID_TOKEN_LINK);
            } else {
                changeUserPassword(user.get(), confirmPassword.getNewPassword(), confirmPassword.getNewPassword());
                actionResult.setErrorCode(ErrorCodeEnum.CHANGE_PASSWORD_SUCCESSFULLY);
            }
        }
        return actionResult;
    }


    @Override
    public ActionResult changeStateActive(String userId) {
        ActionResult actionResult = new ActionResult();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        } else {
            user.get().setIsActive(!user.get().getIsActive());
            user = Optional.of(save(user.get()));
            if (this.exists(Constants.USERS)) this.delete(Constants.USERS);
            actionResult.setData(user);
            actionResult.setErrorCode(ErrorCodeEnum.CHANGE_STATE_SUCCESSFULLY);
        }
        return actionResult;
    }

    @Override
    public ActionResult replyActive(SendMailDTO request) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setLocale(new Locale("vi", "VN"));
        context.setVariable("content", request.getContent());
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setSubject(request.getSubject());
        helper.setTo(request.getSendTo());
        helper.setText(Utils.cleanHTML(request.getContent()), true);
        helper.setFrom(env.getProperty("spring.mail.username"), request.getAuthor());
        javaMailSender.send(message);
        if (this.exists(Constants.USERS)) this.delete(Constants.USERS);
        ActionResult actionResult = new ActionResult();
        actionResult.setErrorCode(ErrorCodeEnum.SEND_MAIL_SUCCESSFULLY);
        return actionResult;
    }

    ActionResult sendInvoice(SendInvoiceRequest request) {
        ActionResult actionResult = new ActionResult();
        Context context = new Context();
        context.setLocale(new Locale("vi", "VN"));

        return actionResult;
    }
    @Override
    public ActionResult settingSendMail(SettingSendMailDto request) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional <User> userOptional = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            if (userOptional.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            } else {
                User user = userOptional.get();
                user.setIsReceiveEmail(request.getIsReceiveEmail());
                if(request.getReceiveEmail()!=null)
                    user.setReceiveEmail(request.getReceiveEmail());
                if(request.getTimeGetData()!=null)
                    user.setTimeGetData(request.getTimeGetData());
                user = save(user);
                actionResult.setErrorCode(ErrorCodeEnum.SETTING_MAIL_SUCCESSFULLY);
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);

        }
        return actionResult;
    }
    @Override
    public ActionResult getSettingMail() {
        ActionResult actionResult = new ActionResult();
        try {
            Optional <User> userOptional = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            if (userOptional.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            } else {
                User user = userOptional.get();
                SettingSendMailDto settingSendMailDto = new SettingSendMailDto();
                settingSendMailDto.setIsReceiveEmail(user.getIsReceiveEmail());
                settingSendMailDto.setReceiveEmail(user.getReceiveEmail());
                settingSendMailDto.setTimeGetData(user.getTimeGetData());
                actionResult.setData(settingSendMailDto);
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
}

