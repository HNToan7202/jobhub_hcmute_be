package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.service.AdminService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@Tag(name="Admin", description="Admin API")
public class AdminController {

    @Autowired
    UserService userService;

    //@PostAuthorize("hasRole('ADMIN')")
    @GetMapping("/hello")
    public ResponseEntity hello(){
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/user/get-list")
    public ResponseEntity getListUser(@RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                      @RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
        return userService.getAccounts(size, page);
    }

}
