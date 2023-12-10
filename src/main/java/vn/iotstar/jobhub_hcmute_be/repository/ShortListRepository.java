package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.ShortList;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.Optional;

@Repository
@Hidden
public interface ShortListRepository extends JpaRepository<ShortList, String> {
    Page<ShortList> findAllByUser_UserId (String userId, Pageable pageable);

    Page<ShortList> findAllByJob_JobId (String jobId, Pageable pageable);

    Page<ShortList> findAllByJob_Employer_UserId(String userId, Pageable pageable);

    Optional<ShortList> findByJob_JobId(String jobId);

    Long countByJob_Employer_UserId(String userId);

    Long countByUser_UserId(String userId);

    Long countByIdIsNotNull();

    Optional<ShortList> findByJob_JobIdAndUser_UserId(String jobId, String userId);
}