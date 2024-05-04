package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iotstar.jobhub_hcmute_be.constant.State;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewJobApplysDTO {
    private String jobApplyId;
    private String userId;
    private String jobId;
    private String email;
    private String phone;
    private String fullName;
    private String avatar;
    private String address;
    private String jobName;
    private String linkCV;
    private String state;
    private Date createdAt;
    public static NewJobApplysDTO transform(JobApply jobApply) {
        return NewJobApplysDTO.builder()
                .jobApplyId(jobApply.getJobApplyId())
                .userId(jobApply.getStudent().getUserId()==null?"":jobApply.getStudent().getUserId())
                .jobId(jobApply.getJob().getJobId())
                .email(jobApply.getEmail())
                .phone(jobApply.getPhone())
                .fullName(jobApply.getFullName())
                .avatar(jobApply.getStudent().getAvatar())
                .address(jobApply.getAddress())
                .jobName(jobApply.getJob().getName())
                .linkCV(jobApply.getResumeUpoad())
                .state(jobApply.getState().name())
                .createdAt(jobApply.getCreatedAt())
                .build();
    }
}
