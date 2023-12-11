package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;

@Data
public class SendMailDTO {
    String author;
    String sendTo;
    String subject;
    String content;
}
