package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
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
    Boolean isEducationsEdited=false;
    Boolean isExperiencesEdited=false;
    Boolean isCertificatesEdited=false;
    Boolean isPrizesEdited  =false;
    Boolean isCoursesEdited=false;
    Boolean isProjectsEdited=false;
    Boolean isSocialActivitiesEdited=false;
    Boolean isSocialsEdited=false;
}