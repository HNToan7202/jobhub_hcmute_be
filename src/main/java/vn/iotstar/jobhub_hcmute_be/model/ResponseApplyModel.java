package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.State;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;

import java.util.List;

@Data
@Builder
public class ResponseApplyModel {

    String jobApplyId;

    String jobId;

    String name;

    String email;

    String phone;

    private String fullName;

    @Nationalized
    private String address;

    private String note;

    String studentId;

    String userId;

    String avatar;

    List<Skill> skills;

    private State state;

    String resumeUpoad;

    public static ResponseApplyModel transform(vn.iotstar.jobhub_hcmute_be.entity.JobApply jobApply) {

        //email: User1@student.hcmute.edu.vn
        //studentId : User1
        return ResponseApplyModel.builder()
                .jobApplyId(jobApply.getJobApplyId())
                .jobId(jobApply.getJob().getJobId())
                .name(jobApply.getJob().getName())
                .email(jobApply.getEmail())
                .phone(jobApply.getPhone())
                .fullName(jobApply.getFullName())
                .address(jobApply.getAddress())
                .note(jobApply.getNote())
                .studentId(jobApply.getEmail().split("@")[0])
                .userId(jobApply.getStudent().getUserId())
                .avatar(jobApply.getStudent().getAvatar())
                .skills(jobApply.getJob().getSkills())
                .state(jobApply.getState())
                .resumeUpoad(jobApply.getResumeUpoad())
                .build();
    }

}
