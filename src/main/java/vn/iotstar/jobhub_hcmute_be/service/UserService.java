package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.EmployerRegisterDTO;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.RegisterRequest;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.Auth.SignUpMailDTO;
import vn.iotstar.jobhub_hcmute_be.dto.PasswordResetRequest;
import vn.iotstar.jobhub_hcmute_be.dto.UserUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.io.IOException;
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

    ResponseEntity<GenericResponse> getProfile(String userId);

    Optional<User> findByEmail(String email);

    ResponseEntity<GenericResponse> userLogin(LoginDTO loginDTO);

    ResponseEntity<GenericResponse> changeUserPassord(User user, PasswordResetRequest request);

    //ResponseEntity<?> employerRegister(EmployerRegisterDTO employerRegisterDTO);

    ResponseEntity<?> employerRegister(EmployerRegisterDTO employerRegisterDTO);

    ResponseEntity<GenericResponse> userRegisterEmail(RegisterRequest RegisterRequest);

    ResponseEntity<GenericResponse> getAccounts(int size, int page);

    ResponseEntity<GenericResponse> getAccounts(String role,int size, int page) throws Exception;

    ResponseEntity<GenericResponse> validateVerificationAccount(String token);

//    ResponseEntity<GenericResponse> changeAvatar(String userId, MultipartFile imageFile) throws IOException;
//
//    ResponseEntity<GenericResponse> updateProfile(String userId, UserUpdateRequest request) throws Exception;

}
