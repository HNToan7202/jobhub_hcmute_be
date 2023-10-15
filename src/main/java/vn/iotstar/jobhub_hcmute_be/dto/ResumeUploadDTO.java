package vn.iotstar.jobhub_hcmute_be.dto;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

public class ResumeUploadDTO {
    MultipartFile resumeFile;
    Boolean isMain;
}
