package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Student;

@Data
@Builder
public class GetFollowerModel {
private String followerId;
    private String userId;
    private String name;
    private String avatar;
    public static GetFollowerModel transformEmployer(Employer employer, String followerId, String userId) {
        return GetFollowerModel.builder()
                .followerId(followerId)
                .userId(userId)
                .name(employer.getCompanyName())
                .avatar(employer.getLogo())
                .build();

    }
    public static GetFollowerModel transformStudent(Student student, String followerId, String userId) {
        return GetFollowerModel.builder()
                .followerId(followerId)
                .userId(student.getUserId())
                .name(student.getFullName())
                .avatar(student.getAvatar())
                .build();

    }

}
