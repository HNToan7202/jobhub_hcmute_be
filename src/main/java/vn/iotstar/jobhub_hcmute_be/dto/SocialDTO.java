package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

@Data
public class SocialDTO {
    private String id;
    private String name;
    private String socialUrl;
    boolean isEdit;
}
