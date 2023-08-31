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

    ResponseEntity<?> employerRegister(EmployerRegisterDTO employerRegisterDTO);

    ResponseEntity<GenericResponse> userRegisterEmail(RegisterRequest RegisterRequest);

    ResponseEntity<GenericResponse> getAccounts(int size, int page) throws Exception;

    ResponseEntity<GenericResponse> validateVerificationAccount(String token);

    ResponseEntity<?> changeAvatar(String userId, MultipartFile imageFile) throws IOException;

    ResponseEntity<Object> updateProfile(String userId, UserUpdateRequest request) throws Exception;
}
