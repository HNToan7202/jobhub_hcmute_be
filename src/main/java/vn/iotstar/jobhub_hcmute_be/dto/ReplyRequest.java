package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.entity.StudentEmail;

import java.io.Serializable;

@Data
public class ReplyRequest implements Serializable {

    String companyName;

    @StudentEmail(message = "Email không hợp lệ")
    String email;

    String subject;

    String content;

}
