package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.UserUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> findAll();

    List<Student> findAllById(Iterable<String> strings);

    <S extends Student> S save(S entity);

    Optional<Student> findById(String s);

    boolean existsById(String s);

    long count();

    void deleteById(String s);

    void delete(Student entity);

    void deleteAll();

    ResponseEntity<GenericResponse> changeAvatar(String userId, MultipartFile imageFile) throws IOException;

    ResponseEntity<GenericResponse> getProfile(String userId);

    ActionResult updateProfile(String userId, UserUpdateRequest request) throws Exception;

    ActionResult getDashBoard(String userId);
}
