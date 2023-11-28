package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Interview;

import java.util.Optional;

@Hidden
@Repository
public interface InterviewRepository extends JpaRepository<Interview, String> {
    Page<Interview> findByJobApply_Job_Employer_UserId(String employerId, Pageable pageable);

}