package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveToMongoDTO {
    @NotNull(message = "Link CV is required")
    String link_cv;

    @NotNull(message = "UserId is required")
    String user_id;

}
