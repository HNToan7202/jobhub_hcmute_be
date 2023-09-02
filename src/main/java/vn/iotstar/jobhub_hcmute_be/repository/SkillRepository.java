package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;

import java.util.Optional;

@Hidden
@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {
    @Query("Select s From Skill s Where s.name = :skillName")
    Optional<Skill> findSkillBySkillName(String skillName);
}