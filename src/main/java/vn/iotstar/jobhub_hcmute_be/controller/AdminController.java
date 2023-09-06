package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerRequest;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.service.AdminService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.Objects;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@Tag(name="Admin", description="Admin API")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;

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

    @GetMapping("/get-list-user")
    public ResponseEntity<GenericResponse> getAccounts(
            @RequestParam(name = "role", required = false, defaultValue = "") String role,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page
    ) throws Exception {
        return userService.getAccounts(role, size, page);
    }

    @PostMapping("/accept-employer")
    public ResponseEntity<?> acceptEmployer(@RequestBody EmployerRequest employerRequest, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult
                            .getFieldError())
                    .getDefaultMessage()
            );
        }
        return adminService.acceptEmployer(employerRequest.getEmployerId());
    }

}
