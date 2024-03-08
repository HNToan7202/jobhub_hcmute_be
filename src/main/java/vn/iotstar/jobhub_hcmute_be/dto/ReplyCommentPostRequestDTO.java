package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class ReplyCommentPostRequestDTO {
    private String content;
    private MultipartFile photos;
    private int commentId;
}
