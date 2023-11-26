package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String fromName;
    private String mailTo;
}
