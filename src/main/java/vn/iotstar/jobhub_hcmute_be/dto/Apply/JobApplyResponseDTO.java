package vn.iotstar.jobhub_hcmute_be.dto.Apply;

import lombok.*;
import vn.iotstar.jobhub_hcmute_be.constant.State;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.JobApply}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplyResponseDTO implements Serializable {
    String jobApplyId;
    String jobId;
    String studentId;
    String userId;
    String avatar;
    String fullName;
    String name;
    String email;

    List<Skill> skills;
    private State state;
    String resumeUpoad;

}