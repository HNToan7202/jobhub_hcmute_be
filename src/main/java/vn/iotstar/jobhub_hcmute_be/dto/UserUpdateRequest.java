package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Value;
import org.hibernate.annotations.Nationalized;

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

    @Nationalized
    private String about;

}
