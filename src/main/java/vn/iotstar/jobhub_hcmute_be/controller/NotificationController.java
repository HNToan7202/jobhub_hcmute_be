package vn.iotstar.jobhub_hcmute_be.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.notification.FirebaseMessageService;
import vn.iotstar.jobhub_hcmute_be.notification.NotificationMessage;

@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    FirebaseMessageService firebaseMessageService;

    @PostMapping
    public String sendNotificationByToken(@RequestBody NotificationMessage notificationMessage){
        return firebaseMessageService.sendNotificationByToken(notificationMessage);
    }
}
