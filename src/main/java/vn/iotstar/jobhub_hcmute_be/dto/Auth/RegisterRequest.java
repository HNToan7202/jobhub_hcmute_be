package vn.iotstar.jobhub_hcmute_be.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.StudentEmail;

@Data
public class RegisterRequest {

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email")
    @StudentEmail(message = "Invalid email format or domain")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Confirm Password is required")
    private String confirmPassword;

    public boolean isStudentEmail() {
        return email != null && email.endsWith("@student.hcmute.edu.vn");
    }
}
