package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.ShortList}
 */
@Value
public class ShortListDto implements Serializable {
    @NotNull
    String jobId;
}