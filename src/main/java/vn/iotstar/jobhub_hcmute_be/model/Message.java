package vn.iotstar.jobhub_hcmute_be.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private String senderName;
    private String receiverName;

    private String messgae;
    private String date;
    private Status status;
}
