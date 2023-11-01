package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.Position;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    Page<Job> findAllByEmployer_UserIdAndIsActiveOrderByCreatedAtDesc(String employerId, Pageable pageable, boolean isActive);

    Page<Job> findAllByEmployer_UserIdOrderByCreatedAtDesc(String employerId, Pageable pageable);

    Page<Job> findAllByEmployer_UserId(String employerId, Pageable pageable);

    List<Job> findAllByEmployer_UserId(String employerId);

    Optional<Job> findJobByNameAndIsActiveIsTrue(String name);

    Page<Job> findByIsActiveOrderByCreatedAtDesc(boolean isActive, Pageable pageable);
    @Query("SELECT j FROM Job j WHERE j.isActive = :isActive")
    List<Job> findJobsByIsActive(boolean isActive);

    @Query("SELECT j FROM Job j WHERE " +
            "(j.name LIKE %:name%) " +
            "AND (COALESCE(:posName, '') = '' OR j.position.name = :posName) " +
            "AND (j.location like %:location%) " +
            "AND j.isActive = true " +
            "ORDER BY j.createdAt DESC")
    Page<Job> findJobs(String name, String posName, String location, Pageable pageable);

}

