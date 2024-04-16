package vn.iotstar.jobhub_hcmute_be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.*;
import vn.iotstar.jobhub_hcmute_be.entity.PasswordResetOtp;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    @Deprecated
    User getById(String s);

    List<User> findAll();

    <S extends User> S save(S entity);

    Optional<User> findById(String s);

    long count();

    void deleteById(String s);

    void delete(User entity);

    List<User> findAll(Sort sort);

    Page<User> findAll(Pageable pageable);

    ActionResult getProfile(String userId) throws JsonProcessingException;

    ActionResult getProfileAdmin(String userId) throws JsonProcessingException;

    Optional<User> findByEmail(String email);

    ResponseEntity<GenericResponse> userLogin(LoginDTO loginDTO);

    ActionResult login(LoginReq loginDTO);

    ResponseEntity<GenericResponse> changeUserPassord(User user, PasswordResetRequest request);

    //ResponseEntity<?> employerRegister(EmployerRegisterDTO employerRegisterDTO);

    ResponseEntity<?> employerRegister(EmployerRegisterDTO employerRegisterDTO);

    ResponseEntity<GenericResponse> userRegisterEmail(RegisterRequest RegisterRequest);

    ResponseEntity<GenericResponse> getAccounts(int size, int page);

    ActionResult getAccounts(int size, int page, String role, Boolean isActive) throws Exception;

    ResponseEntity<GenericResponse> validateVerificationAccount(String token);

    void createPasswordResetOtpForUser(User user, String otp);

    String validatePasswordResetOtp(String otp);

    Optional<PasswordResetOtp> getUserByPasswordResetOtp(String otp);

    void changeUserPassword(User user, String newPassword, String confirmPassword);

    ActionResult getAllEmployer(Pageable pageable, String companyName, String address, String teamSize) throws JsonProcessingException;

    ActionResult detailProfileEmployer(String employerId) throws JsonProcessingException;

    ActionResult confirmPassword(ConfirmPassword confirmPassword);

    ActionResult changeStateActive(String userId);

    ActionResult replyActive(SendMailDTO request) throws MessagingException, UnsupportedEncodingException;

//    ResponseEntity<GenericResponse> changeAvatar(String userId, MultipartFile imageFile) throws IOException;
//
//    ResponseEntity<GenericResponse> updateProfile(String userId, UserUpdateRequest request) throws Exception;

}
