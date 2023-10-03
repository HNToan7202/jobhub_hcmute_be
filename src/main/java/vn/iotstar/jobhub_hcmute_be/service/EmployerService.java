package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerUpdateDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.UserUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public interface EmployerService {
    <S extends Employer> S save(S entity);

    Optional<Employer> findById(String s);

    boolean existsById(String s);

    long count();

    void deleteById(String s);

    void delete(Employer entity);

    <S extends Employer, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    ResponseEntity<GenericResponse> changeLogo(String userId, MultipartFile imageFile) throws IOException;

    ResponseEntity<GenericResponse> updateCompanyProfile(String userId, EmployerUpdateDTO request) throws Exception;
}
