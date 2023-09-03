package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.entity.ResumeUpload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Resume}
 */
@Value
public class ResumeDTO implements Serializable {
    String education;
    String experience;
    String certificate;
    String prize;
    String course;
    String project;
    String socialActivity;
    Date createAt;
    Date updateAt;
    Boolean isActive;
}