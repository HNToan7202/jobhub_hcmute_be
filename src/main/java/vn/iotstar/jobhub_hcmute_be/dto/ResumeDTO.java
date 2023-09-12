package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.entity.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Resume}
 */
@Value
public class ResumeDTO implements Serializable {
    Education education;
    Experience experience;
    Certificate certificate;
    Prize prize;
    Course course;
    Project project;
    SocialActivity socialActivity;
    Social social;
    Date createAt;
    Date updateAt;
    Boolean isActive;

    //List<ResumeUpload> resumeUploads;
}