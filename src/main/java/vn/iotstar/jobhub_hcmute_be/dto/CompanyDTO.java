package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class CompanyDTO {
    private String companyName;
    private String logo;

    private String website;

    private String foundedYear;

    private String teamSize;

    private String userId;


    private String phone;

    private String about;

    @Email
    private String email;

    private List<String> address;
}
