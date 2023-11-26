package vn.iotstar.jobhub_hcmute_be.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import vn.iotstar.jobhub_hcmute_be.dto.NotificationRequest;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    ActionResult notificationMessage(NotificationRequest request) throws MessagingException, UnsupportedEncodingException;
}
