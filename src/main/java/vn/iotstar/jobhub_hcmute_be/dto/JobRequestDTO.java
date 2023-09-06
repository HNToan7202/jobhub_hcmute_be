package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * DTO for {@link vn.iotstar.jobhub_hcmute_be.entity.Job}
 */
@Value
public class JobRequestDTO implements Serializable {

    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotNull(message = "jobType cannot be empty")
    String jobType;
    String time;
    String link;
    //String logo;
    @Min(value = 1,message = "Min of quantity is 1")
    Integer quantity;
    String benefit;
    String salaryRange;
    String requirement;
    String location;
    String description;
    Date deadline;
    @NotNull
    private String positionName;

    @NotNull
    private List<String> skillsRequired;

}