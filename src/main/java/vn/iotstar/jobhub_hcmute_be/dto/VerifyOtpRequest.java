package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Value
public class VerifyOtpRequest {

    @Email(message = "Invalid email format")
    String email;

    @NotBlank
    @NotNull
    String otp;
}
