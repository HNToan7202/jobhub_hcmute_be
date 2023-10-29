package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;
import vn.iotstar.jobhub_hcmute_be.entity.Student;

import java.util.Date;

@Hidden
@Repository
public interface JobApplyRepository extends JpaRepository<JobApply, String> {
    JobApply findByStudentAndJob(Student student, Job job);

    Page<JobApply> findAllByStudent_UserIdOrderByCreatedAtDesc(Pageable pageable, String userId);

    Page<JobApply> findAllByJob_Employer_UserId(Pageable pageable, String userId);

    Page<JobApply> findAllByJob_Employer_UserIdAndCreatedAtBetween(
            Pageable pageable,
            String userId,
            Date startDate,
            Date endDate
    );

}