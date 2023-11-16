package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.entity.Event;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * DTO for {@link Event}
 */
@Data
public class EventDto implements Serializable {
    String title;
    String description;
    String location;
    String img;
    private LocalDate startAt;
    private LocalDate deadline;

    @AssertTrue(message = "Start date must be before deadline")
    public boolean isValid() {
        return this.startAt.isBefore(this.deadline);
    }
}