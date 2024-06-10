package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CoursesDTO {
    @NotNull(message = "Course name cannot be blank")
    private String courseName;
    @NotNull(message = "Course link cannot be blank")
    private String courseLink;
    private String image;
    private String description;
    @NotNull(message = "Course type cannot be blank")
    private String type;
}
