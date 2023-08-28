package vn.iotstar.jobhub_hcmute_be.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
public class AdminController {

    @PostAuthorize("hasRole('ADMIN')")
    @GetMapping("/hello")
    public ResponseEntity hello(){
        return ResponseEntity.ok("hello");
    }

}
