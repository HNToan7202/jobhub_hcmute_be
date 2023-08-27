package vn.iotstar.jobhub_hcmute_be.dto;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDTO {

    @NotBlank
    @NotEmpty
    private String userLogin; //email or phone

    @NotBlank
    @NotEmpty
    private String password;

}
