package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    @Deprecated
    Admin getOne(String s);

    <S extends Admin> List<S> findAll(Example<S> example);

    List<Admin> findAll();

    <S extends Admin> S save(S entity);

    Optional<Admin> findById(String s);

    void deleteById(String s);

    ResponseEntity<?> acceptEmployer(String employerId);
}
