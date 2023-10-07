package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.PostJobRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {
    <S extends Job> List<S> saveAll(Iterable<S> entities);

    List<Job> findAll();

    ResponseEntity<GenericResponse> getAlls(Boolean isActive);

    <S extends Job> S save(S entity);

    Optional<Job> findById(String s);

    long count();

    void deleteById(String s);

    void delete(Job entity);

    void deleteAll();

    List<Job> findAll(Sort sort);

    Page<Job> findAll(Pageable pageable);


    ResponseEntity<?> getDetail(String jobId);

    ResponseEntity<?> findAllByEmployer(String id, Pageable pageable);

    Page<Job> findAllByIsActiveIsTrueOrderByCreatedAtDesc(Boolean isActive, Pageable pageable);

    List<Job> findAllByIsActive(Boolean isActive);

    ResponseEntity<GenericResponse> getAllJobs(Pageable pageable, Boolean isActive);

    ResponseEntity<?> postJob(PostJobRequest jobRequest, String recruiterId);
}
