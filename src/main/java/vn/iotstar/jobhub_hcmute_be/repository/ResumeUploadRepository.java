package vn.iotstar.jobhub_hcmute_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.ResumeUpload;

public interface ResumeUploadRepository extends JpaRepository<ResumeUpload, String> {
}