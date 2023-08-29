package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
@Value
public class LoginDTO {

    @NotBlank
    @NotEmpty
    private String userLogin; //email or phone

    @NotBlank
    @NotEmpty
    private String password;

}