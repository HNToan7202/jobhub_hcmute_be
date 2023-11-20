package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Employer}
 */
@Data
public class EmployerDTO implements Serializable {
    String userId;
    String phone;
    @Email
    String email;
    boolean isVerified;
    String about;
    Boolean isActive;
    Date lastLoginAt;
    Map<String, String> linkContacts;
    EmployState employState;
    String companyName;
    List<String> backGround;
    List<String> address;
    String logo;
    String website;
    String foundedYear;
    String teamSize;
    Long transactionMoney;
}