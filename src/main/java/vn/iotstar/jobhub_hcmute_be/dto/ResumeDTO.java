package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.*;

import java.util.Date;
import java.util.List;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Resume}
 */
@Data
public class ResumeDTO{
    List<EducationDTO> educations;
    List<ExperienceDTO> experiences;
    List<CertificateDTO> certificates;
    List<PrizeDTO> prizes;
    List<CourseDTO> courses;
    List<ProjectDTO> projects;
    List<SocialActivityDTO> socialActivities;
    List<SocialDTO> socials;
    Date createAt;
    Date updateAt;
    Boolean isActive;
    //List<ResumeUpload> resumeUploads;
}