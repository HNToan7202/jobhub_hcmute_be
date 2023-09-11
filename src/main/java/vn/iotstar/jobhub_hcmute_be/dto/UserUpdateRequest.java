package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Value;
import org.hibernate.annotations.Nationalized;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;

import java.util.Date;

@Value
public class UserUpdateRequest {
    @Nationalized
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Nationalized
    @NotBlank(message = "Address is required")
    private String address;

    private Date dateOfBirth;

    @Pattern(regexp = "^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})$", message = "Invalid phone number")
    private String phone;

    private Gender gender;

    @Nationalized
    private String about;

}
