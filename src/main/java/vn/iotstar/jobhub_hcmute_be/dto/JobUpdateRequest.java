package vn.iotstar.jobhub_hcmute_be.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobUpdateRequest {
    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotNull(message = "jobType cannot be empty")
    String jobType;
    String time;
    String link;
    @Min(value = 1,message = "Min of quantity is 1")
    Integer quantity;
    String benefit;
    String salaryRange;
    Integer experience;
    String requirement;
    String location;
    String description;
    Date deadline;
    @NotNull
    private String positionName;

    @NotNull
    private List<String> skillsRequired;
}
