package vn.iotstar.jobhub_hcmute_be.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.enums.PrivacyLevel;

import java.util.Date;
import java.util.List;

@Data
public class CreatePostRequestDTO {
    @NotNull(message = "Location id is required")
    private String location;
    @NotNull(message = "Location id is required")
    private String content;
    private List<MultipartFile> photos;
    private MultipartFile files;
    private PrivacyLevel privacyLevel;

}