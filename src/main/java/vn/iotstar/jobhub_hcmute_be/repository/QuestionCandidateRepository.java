package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.QuestionCandidate;
import vn.iotstar.jobhub_hcmute_be.entity.QuestionCandidateId;
@Hidden
@Repository
public interface QuestionCandidateRepository extends JpaRepository<QuestionCandidate, QuestionCandidateId> {
}