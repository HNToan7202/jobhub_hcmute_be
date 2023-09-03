package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Value
public class ResumeUploadRequest {

    MultipartFile resumeFile;
}
