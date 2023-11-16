package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;

@Getter
@Setter
public class EmployerRequest {
    String employerId;

    @Enumerated(EnumType.STRING)
    private EmployState employState;

}
