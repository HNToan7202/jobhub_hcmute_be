package vn.iotstar.jobhub_hcmute_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.model.Message;

@RestController
public class ChatController {

    SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/message") // app/message
    @SendTo("/chatroom/public")
    public Message receiveMessagePublicMessage(@Payload Message message) {
        return message;
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        return message;
    }
}
