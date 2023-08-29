package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="Home", description="Home API")
public class HomeController {
    @GetMapping("/")
    public String helloWorld(){
        return "Hello World, This is Group 8!";
    }
}
