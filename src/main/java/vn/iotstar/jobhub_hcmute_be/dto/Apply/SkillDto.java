package vn.iotstar.jobhub_hcmute_be.dto.Apply;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Skill}
 */
@Value
public class SkillDto implements Serializable {
    String skillId;
    String name;
}