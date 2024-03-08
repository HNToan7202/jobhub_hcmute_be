package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Student;

@Data
@Builder
public class GetFriendsModel {
    private int friendId;
    private String userId;
    private String name;
    private String avatar;
    public static GetFriendsModel transformEmployer(Employer employer, int friendId, String userId) {
        return GetFriendsModel.builder()
                .friendId(friendId)
                .userId(userId)
                .name(employer.getCompanyName())
                .avatar(employer.getLogo())
                .build();

    }
    public static GetFriendsModel transformStudent(Student student, int friendId, String userId) {
        return GetFriendsModel.builder()
                .friendId(friendId)
                .userId(student.getUserId())
                .name(student.getFullName())
                .avatar(student.getAvatar())
                .build();

    }
}
