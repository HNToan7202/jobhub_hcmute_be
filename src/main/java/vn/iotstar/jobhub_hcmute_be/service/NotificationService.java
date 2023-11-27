package vn.iotstar.jobhub_hcmute_be.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import vn.iotstar.jobhub_hcmute_be.dto.NotificationRequest;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.io.UnsupportedEncodingException;

public interface NotificationService {

    @Async
    void sendApplyJob(String mail, String student, String jobName);

    ActionResult notificationMessage(NotificationRequest request) throws MessagingException, UnsupportedEncodingException;
}
