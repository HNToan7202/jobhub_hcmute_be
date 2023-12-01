package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EmployerUpdateDTO {

    @NotNull(message = "Company name is required")
    @NotEmpty(message = "Company name is required")
    @NotBlank
    private String companyName;

    private List<String> address;

    private String website;

    @NotNull(message = "Founded year is required")
    @NotEmpty(message = "Founded year is required")
    @NotBlank
    private String foundedYear;

    @NotNull(message = "Team size is required")
    @NotEmpty(message = "Team size is required")
    private String teamSize;

    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Phone number is invalid")
    @NotNull(message = "Phone number is required")
    @NotEmpty(message = "Phone number is required")
    private String phone;

    //private String email;

    private String about;

    Map<String, String> linkContacts;

}
