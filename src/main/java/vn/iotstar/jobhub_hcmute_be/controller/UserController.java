package vn.iotstar.jobhub_hcmute_be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.*;

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

    @Autowired
    ResumeService resumeService;

    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    NotificationService notificationService;


    public UserController(JwtTokenProvider jwtTokenProvider, UserService userService, CloudinaryService cloudinaryService, EmailVerificationService emailVerificationService, UserRepository userRepository, StudentService studentService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.emailVerificationService = emailVerificationService;
        this.userRepository = userRepository;
        this.studentService = studentService;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        boolean isOtpVerified = emailVerificationService.verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
        if (isOtpVerified) {
            return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("OTP verified successfully!").result(null).statusCode(HttpStatus.OK.value()).build());
        } else if (userRepository.findByEmail(verifyOtpRequest.getEmail()).isPresent()) {
            User user = userRepository.findByEmail(verifyOtpRequest.getEmail()).get();
            if (user.isVerified()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(true).message("Account is already verified!").statusCode(HttpStatus.NOT_FOUND.value()).build());
            } else {
                //set verified
                user.setVerified(true);
                userRepository.save(user);
                return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("OTP verified successfully!").result(null).statusCode(HttpStatus.OK.value()).build());
            }

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

    @GetMapping("{userId}/detail-resume")
    public ResponseEntity<?> getDetailResume(@PathVariable String userId) {
        User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return resumeService.getDetailResume(user.getUserId());
    }

    @GetMapping("{userId}/profile")
    public ResponseModel getProfile(@PathVariable String userId) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.getProfile(userId);
        } catch (ClassCastException | JsonProcessingException e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    //companyName, address, teamSize

    @GetMapping("/get-all-employer")
    public ResponseModel getAllEmployer(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "index", required = false, defaultValue = "0") int index,
            @RequestParam(name = "companyName", defaultValue = "") String companyName,
            @RequestParam(name = "address", defaultValue = "") String address,
            @RequestParam(name = "teamSize", defaultValue = "") String teamSize
    ) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.getAllEmployer(PageRequest.of(index, size), companyName, address, teamSize);
        } catch (ClassCastException e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("{employerId}/detail-employer")
    public ResponseModel getDetailEmployer(@PathVariable String employerId) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.detailProfileEmployer(employerId);
        } catch (ClassCastException e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("{studentId}/cv-upload")
    public ResponseEntity<?> getResume(
            @PathVariable String studentId) {
        User user = userService.findById(studentId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + studentId));
        return resumeService.getResumeUpload(user.getUserId());
    }

    @PostMapping("notify-message")
    public ResponseModel sendMailMessage(@RequestBody NotificationRequest request) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = notificationService.notificationMessage(request);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

}
