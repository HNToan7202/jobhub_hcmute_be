package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    private String foundedYear;

    private String teamSize;

    private String phone;

    //private String email;

    private String about;

    Map<String, String> linkContacts;

}
