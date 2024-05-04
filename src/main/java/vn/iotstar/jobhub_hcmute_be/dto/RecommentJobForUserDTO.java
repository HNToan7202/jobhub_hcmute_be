package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommentJobForUserDTO {
    private String jobId;
    private String name;
    private String jobType;
    private String logo;
    private Integer experience;
    private Integer quantity;
    private String location;
    private LocalDate createdAt;
    private LocalDate deadline;
    private String position;
    private String skills;
    private String salaryRange;
    public static RecommentJobForUserDTO transform(Job job) {
        return RecommentJobForUserDTO.builder()
                .jobId(job.getJobId())
                .name(job.getName())
                .jobType(job.getJobType().name())
                .logo(job.getLogo())
                .experience(job.getExperience())
                .quantity(job.getQuantity())
                .location(job.getLocation())
                .createdAt(job.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .deadline(job.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .position(job.getPosition().getName())
                .skills(job.getSkills().stream().map(Skill::getName).reduce((a, b) -> a + ", " + b).orElse(""))
                .salaryRange(job.getSalaryRange())
                .build();
    }
}
