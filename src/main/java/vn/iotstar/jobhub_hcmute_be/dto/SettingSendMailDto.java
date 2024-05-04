package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class SettingSendMailDto {
    @NotNull(message = "Time get data is required")
    private Boolean isReceiveEmail;
    private String receiveEmail;
    private Integer timeGetData;
}
