package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.StudentEmail;

@Data
public class ForgotPasswordto {
    @Email(message = "Email is not valid")
    @NotNull
    @NotEmpty
    String email;
}
