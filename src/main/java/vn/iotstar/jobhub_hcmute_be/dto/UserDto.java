package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.User}
 */
@Value
public class UserDto implements Serializable {
    String userId;
}