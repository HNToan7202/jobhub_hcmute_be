package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.SignUpMailDTO;
import vn.iotstar.jobhub_hcmute_be.entity.User;

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

    ResponseEntity<GenericResponse> getProfile(String userId);

    Optional<User> findByEmail(String email);

    ResponseEntity<GenericResponse> userLogin(LoginDTO loginDTO);

    ResponseEntity<GenericResponse> userRegisterEmail(SignUpMailDTO signUpMailDTO);
}
