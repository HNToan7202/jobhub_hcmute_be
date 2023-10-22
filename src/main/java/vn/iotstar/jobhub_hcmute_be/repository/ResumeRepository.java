package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Resume;

import java.util.Optional;

@Hidden
@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {
    Optional<Resume> findByStudent_UserId(String userId);
}