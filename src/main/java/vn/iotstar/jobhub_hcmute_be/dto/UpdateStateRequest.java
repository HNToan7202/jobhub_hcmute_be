package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.*;
@Data
public class UpdateStateRequest {
    private String jobId;
    private int status;
}
