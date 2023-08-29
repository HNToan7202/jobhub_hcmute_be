package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.*;

@Value
@Builder
public class GenericResponse {
    private Boolean success;
    private String message;
    private Object result;
    private int statusCode;
}
