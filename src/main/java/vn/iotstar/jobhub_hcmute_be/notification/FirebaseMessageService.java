package vn.iotstar.jobhub_hcmute_be.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessageService {

    @Autowired
    FirebaseMessaging firebaseMessaging;//đối tượng để gửi message

    public String sendNotificationByToken(NotificationMessage notificationMessage){
        //tạo đối tượng notification để gửi
        Notification notification= Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();
        //tạo đối tượng message để gửi
        Message message = Message
                .builder()
                .setToken(notificationMessage.getRecipientToken())
                .setNotification(notification)
                .putData("click_action", notificationMessage.getClickAction())
                .putAllData(notificationMessage.getData())
                .build();
        try{
            firebaseMessaging.send(message);
            return "Success";

        }catch (FirebaseMessagingException fme){
            fme.printStackTrace();
            return "err";
        }
    }

}