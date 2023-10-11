package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.*;

@Value
@Builder
public class GenericResponse {
    Boolean success;
    String message;
    Object result;
    int statusCode;
}
