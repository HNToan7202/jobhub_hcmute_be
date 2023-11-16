package vn.iotstar.jobhub_hcmute_be.notification;

import lombok.Data;

import java.util.Map;

@Data
public class NotificationMessage {
    private String recipientToken;
    private String title;
    private String body;
    private String image;
    private String clickAction;
    private Map<String, String> data;
}