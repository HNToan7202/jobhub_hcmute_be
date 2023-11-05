package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

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

    Map<String, String> linkContacts;

}
