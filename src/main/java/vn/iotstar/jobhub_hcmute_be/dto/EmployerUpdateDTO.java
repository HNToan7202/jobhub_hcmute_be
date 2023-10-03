package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployerUpdateDTO {

    private String companyName;

    private List<String> address;

    private String website;

    private String foundedYear;

    private String teamSize;

    private String phone;

    //private String email;

    private String about;

}
