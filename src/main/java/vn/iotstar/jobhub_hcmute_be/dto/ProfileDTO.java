package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;
import vn.iotstar.jobhub_hcmute_be.entity.Role;

import java.util.Date;

@Data
public class ProfileDTO {
    String phone;
    String address;
    String fullName;
    String email;
    String avatar;
    Role role;
    String isActive;
    Date lastLoginAt;
    String studentId;
    Gender gender;
    String about;
    Date dateOfBirth;
}
