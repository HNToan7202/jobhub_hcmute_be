package vn.iotstar.jobhub_hcmute_be.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.entity.StudentEmail;

@Value
public class SignUpMailDTO {
    @Email(message = "Invalid email format")
    @StudentEmail(message = "Invalid email format or domain")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

}