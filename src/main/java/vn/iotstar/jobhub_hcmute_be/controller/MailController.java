package vn.iotstar.jobhub_hcmute_be.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@Validated
@Tag(name = "Mail", description = "Mail API")
public class MailController {

    
}
