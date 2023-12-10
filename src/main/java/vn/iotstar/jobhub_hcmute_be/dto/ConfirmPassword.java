package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ConfirmPassword {

    @Email(message = "Email is not valid")
    private String email;

    @NotNull(message = "OTP is not null")
    @NotEmpty(message = "OTP is not empty")
    private String otp;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters long")
    private String newPassword;
    
}
