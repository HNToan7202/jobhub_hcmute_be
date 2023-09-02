package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Job;

@Hidden
@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    Page<Job> findAllByEmployer_UserIdAndIsActiveIsTrueOrderByCreatedAtDesc(String id, Pageable pageable);
}