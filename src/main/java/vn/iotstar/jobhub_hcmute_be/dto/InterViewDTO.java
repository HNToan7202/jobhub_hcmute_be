package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class InterViewDTO {
    @NotNull(message = "Time id is required")
    private Date time;
    @NotNull(message = "Event id is required")
    private String startTime;
    @NotNull(message = "Event id is required")
    private String endTime;
    private String interviewLink;
    private String note;
}
