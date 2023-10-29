package vn.iotstar.jobhub_hcmute_be.dto.Apply;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Student}
 */
@Value
public class StudentDto implements Serializable {
    String userId;
    String fullName;
    String avatar;
}