package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import java.util.Date;

@Data
@Builder
public class ResponUserModel {
    String userId;
    String phone;
    String address;
    String fullName;
    String email;
    String avatar;
    Boolean isActive;
    Date lastLoginAt;
    String studentId;
    Gender gender;
    String about;
    Date dateOfBirth;
    public static ResponUserModel transform (Student student) {
        return ResponUserModel.builder()
                .userId(student.getUserId())
                .phone(student.getPhone())
                .address(student.getAddress())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .avatar(student.getAvatar())
                .isActive(student.getIsActive())
                .lastLoginAt(student.getLastLoginAt())
                .studentId(student.getStudentId())
                .build();
    }
}
