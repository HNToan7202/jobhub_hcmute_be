package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Job}
 */
@Data
public class JobModel implements Serializable {
    String jobId;
    String name;
    JobType jobType;
    String time;
    String link;
    String logo;
    Integer experience;
    Integer quantity;
    String benefit;
    String salaryRange;
    String requirement;
    String location;
    String description;
    Date deadline;
    int status;
}