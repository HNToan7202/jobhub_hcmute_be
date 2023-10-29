package vn.iotstar.jobhub_hcmute_be.dto.Apply;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Job}
 */
@Value
public class JobDto implements Serializable {
    List<SkillDto> skills;
}