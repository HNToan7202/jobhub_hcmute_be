package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;

import java.util.Date;

@Data
public class JobApplyResponse {
    private String jobApplyId;

    private String email;

    private String phone;

    private String fullName;

    private String address;

    private String note;

    private Date createdAt;

    private Date updatedAt;

    private String resumeUpoad;

    private String jobId;

    private String userId;

    public static JobApplyResponse transform(JobApply jobApply) {
        JobApplyResponse jobApplyResponse = new JobApplyResponse();
        jobApplyResponse.setJobApplyId(jobApply.getJobApplyId());
        jobApplyResponse.setEmail(jobApply.getEmail());
        jobApplyResponse.setPhone(jobApply.getPhone());
        jobApplyResponse.setFullName(jobApply.getFullName());
        jobApplyResponse.setAddress(jobApply.getAddress());
        jobApplyResponse.setNote(jobApply.getNote());
        jobApplyResponse.setCreatedAt(jobApply.getCreatedAt());
        jobApplyResponse.setUpdatedAt(jobApply.getUpdatedAt());
        jobApplyResponse.setResumeUpoad(jobApply.getResumeUpoad());
        jobApplyResponse.setJobId(jobApply.getJob().getJobId());
        jobApplyResponse.setUserId(jobApply.getStudent().getUserId());
        return jobApplyResponse;
    }

}
