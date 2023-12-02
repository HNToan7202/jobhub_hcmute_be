package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.ResumeUpload;

@Hidden
@Repository
public interface ResumeUploadRepository extends JpaRepository<ResumeUpload, String> {
    Long countByResume_Student_UserIdAndIsActiveIsTrue(String userId);
}