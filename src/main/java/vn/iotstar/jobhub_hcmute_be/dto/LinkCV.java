package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LinkCV {
    @NotNull(message = "Link CV is required")
    private String link_cv;
}
