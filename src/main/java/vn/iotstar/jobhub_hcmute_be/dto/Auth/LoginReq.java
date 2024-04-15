package vn.iotstar.jobhub_hcmute_be.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class LoginReq {

    @NotBlank
    @NotEmpty
    private String userLogin; //email or phone

    @NotBlank
    @NotEmpty
    private String password;

}