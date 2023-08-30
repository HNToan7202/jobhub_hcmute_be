package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.jetbrains.annotations.BlockingExecutor;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationRequest {

    @Email(message = "Invalid email format")
    String email;
}
