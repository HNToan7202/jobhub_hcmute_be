package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {

    @NotNull
    @NotBlank
    @Size(min = 8, max = 32, message = "Password not valid")
    private  String oldPassword;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters long")
    private String newPassword;

    @NotNull
    @NotBlank
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match")
    private boolean isPasswordMatching() {
        // Kiểm tra xem newPassword và confirmPassword có giống nhau không
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
