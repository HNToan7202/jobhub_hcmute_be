package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class SignUpMailDTO {
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    public boolean isStudentEmail() {
        return email != null && email.endsWith("@student.hcmute.edu.vn");
    }
}