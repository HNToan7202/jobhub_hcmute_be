package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PositionDTO {

    @NotNull
    @NotBlank
    private String icon;

    @NotNull
    @NotBlank
    private String name;
    
    private Long amountPosition = 0L;
}
