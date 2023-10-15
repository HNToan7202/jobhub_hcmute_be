package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;

import java.util.List;
import java.util.Optional;

public interface SkillService {
    @Query("Select s From Skill s Where s.name = :skillName")
    Optional<Skill> findSkillBySkillName(String skillName);

    List<Skill> findAll();

    Optional<Skill> findById(String s);

    void deleteById(String s);

    List<Skill> findAll(Sort sort);

    Page<Skill> findAll(Pageable pageable);

    ResponseEntity<GenericResponse> getAllSkill();
}
