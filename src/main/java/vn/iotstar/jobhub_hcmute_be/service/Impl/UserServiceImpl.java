package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;
import vn.iotstar.jobhub_hcmute_be.constant.Utils;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.EmployerRegisterDTO;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.RegisterRequest;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.exception.UserNotFoundException;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.*;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.EmailVerificationService;
import vn.iotstar.jobhub_hcmute_be.service.RefreshTokenService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@Transactional
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


    //Đánh node các hàm dưới đây
    @Override
    public ActionResult getProfile(String userId) {
        ActionResult actionResult = new ActionResult(); //(1)
        Optional<Student> student = studentRepository.findById(userId); //(2)

        if (student.isEmpty()) //(3)
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND); //(4)
        else {
            ProfileDTO profileDTO = new ProfileDTO(); //(5)
            BeanUtils.copyProperties(student.get(), profileDTO); //(6)
            //cắt chuỗi mail trước dấu @
            actionResult.setData(student); //(7)
            actionResult.setErrorCode(ErrorCodeEnum.GET_PROFILE_SUCCESSFULLY); //(8)
        }
        return actionResult; //(9)
    }

    @Override
    public ActionResult getProfileAdmin(String userId) {
        ActionResult actionResult = new ActionResult();
        Optional<Admin> student = adminRepository.findById(userId);
        if (student.isEmpty())
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        else {
            ProfileDTO profileDTO = new ProfileDTO();
            BeanUtils.copyProperties(student.get(), profileDTO);
            actionResult.setData(profileDTO);
            actionResult.setErrorCode(ErrorCodeEnum.GET_ADMIN_PROFILE_SUCCESSFULLY);
        }
        return actionResult;
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

        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Login successfully!")
                .result(tokenMap)
                .statusCode(HttpStatus.OK.value())
                .build());

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

    @Cacheable("users")
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
    public ActionResult getAllEmployer(Pageable pageable, String companyName, String address, String teamSize) {
        ActionResult actionResult = new ActionResult();
        Page<Employer> employers = employerRepository.findEmployers(pageable, companyName, teamSize);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("employers", employers.getContent());
        map.put("pageNumber", employers.getPageable().getPageNumber());
        map.put("pageSize", employers.getSize());
        map.put("totalPages", employers.getTotalPages());
        map.put("totalElements", employers.getTotalElements());
        actionResult.setData(map);
        actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_EMPLOYER_SUCCESSFULLY);
        return actionResult;
    }

    @Override
    public ActionResult detailProfileEmployer(String employerId) {
        ActionResult actionResult = new ActionResult();
        Optional<Employer> employer = employerRepository.findById(employerId);
        if (employer.isEmpty()) {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        } else {
            EmployerDTO employerDTO = new EmployerDTO();
            BeanUtils.copyProperties(employer.get(), employerDTO);
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
        ActionResult actionResult = new ActionResult();
        actionResult.setErrorCode(ErrorCodeEnum.SEND_MAIL_SUCCESSFULLY);
        return actionResult;
    }
}

