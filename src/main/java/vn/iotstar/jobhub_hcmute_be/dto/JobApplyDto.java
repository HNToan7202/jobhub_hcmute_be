package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.constant.State;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.JobApply}
 */
@Data
public class JobApplyDto implements Serializable {
    String jobApplyId;
    String nameJob;
    String logoJob;
    String locationJob;
    String email;
    String phone;
    String fullName;
    String address;
    String note;
    Date createdAt;
    Date updatedAt;
    State state;
    String resumeUpoad;


}