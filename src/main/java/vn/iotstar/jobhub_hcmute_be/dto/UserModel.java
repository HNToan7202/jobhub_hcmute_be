package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.entity.Resume;

import java.io.Serializable;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.User}
 */
@Data
public class UserModel implements Serializable {
    String userId;
    String phone;
    String fullName;
    String avatar;
    @Email
    String email;
    String about;
}