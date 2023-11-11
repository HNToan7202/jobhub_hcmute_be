package vn.iotstar.jobhub_hcmute_be.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @GetMapping("/pay")
    public String getPay(){
        return "";
    }

}
