package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

@Data
public class ProfileDTO {
    String phone;
    String address;
    String note;
    String fullName;
    String email;
    String avatar;
    String role;
    String state;
    String createdAt;
    String updatedAt;

    String studentId;

    String gender;
}
