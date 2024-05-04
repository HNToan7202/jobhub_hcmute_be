package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.service.Impl.MailServiceVs2Impl;

@RestController
@RequestMapping("/api/v1/sendMail")
@Tag(name = "sendMail", description = "Send Mail API")
public class SendMailController {
    @Autowired
    private MailServiceVs2Impl mailService;

    @GetMapping("/listUser/{page}")
    public void sendMail(@PathVariable("page") Integer page){
        mailService.sendNewUser(page, 100);
    }
    @GetMapping("/sendMailJobApplice")
    public void sendMailJobApplice(){
        mailService.sendJobApplied();
    }
    //sendRecommentJobForUser
    @GetMapping("/sendRecommentJobForUser")
    public void sendRecommentJobForUser(){
        mailService.sendRecommentJobForUser();
    }

}
