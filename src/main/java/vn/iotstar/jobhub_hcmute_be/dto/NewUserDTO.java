package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserDTO {
    private String userId;
    private String email;
    private String phone;

    private String fullName;
    private String avatar;

    private boolean isVerified;
    private boolean isActive;
    private Date createdAt;
    private Date lastLoginAt;


}
