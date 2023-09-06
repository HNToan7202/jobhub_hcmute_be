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

    ResponseEntity<GenericResponse> getAllJobs(Pageable pageable);

    ResponseEntity<?> postJob(PostJobRequest jobRequest, String recruiterId, String avatarUrl);
}
