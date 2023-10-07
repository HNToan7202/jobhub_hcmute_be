package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Job;

import java.util.List;

@Hidden
@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    Page<Job> findAllByEmployer_UserIdAndIsActiveIsTrueOrderByCreatedAtDesc(String id, Pageable pageable);

    Page<Job> findByIsActiveOrderByCreatedAtDesc(boolean isActive, Pageable pageable);
    @Query("SELECT j FROM Job j WHERE j.isActive = :isActive")
    List<Job> findJobsByIsActive(boolean isActive);
}