package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;
@Data
public class UpdateCommentRequestDTO {
    @Nationalized
    @NotBlank(message = "Content is required")
    private String content;

    @Nationalized
    private MultipartFile photos;
}
