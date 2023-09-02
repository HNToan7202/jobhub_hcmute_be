package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostJobRequest {
    @NotBlank(message = "Name cannot be blank")
    private  String name;

//    @NotBlank(message = "jobTitle cannot be blank")
//    private  String jobTitle;

    @NotNull(message = "jobType cannot be empty")
    private  String jobType;

    @Min(value = 1,message = "Min of quantity is 1")
    private  int quantity;

    private String benefit;
    private  String salaryRange;
    private String requirement;
    private  String location;
    private String description;

    @NotNull
    @Future
    private Date deadline;

    @NotNull
    private String positionName;

    @NotNull
    private List<String> skillsRequired;
}
