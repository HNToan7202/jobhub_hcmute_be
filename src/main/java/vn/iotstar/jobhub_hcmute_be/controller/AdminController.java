package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.ActiveUserRequest;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerRequest;
import vn.iotstar.jobhub_hcmute_be.dto.EventDto;
import vn.iotstar.jobhub_hcmute_be.dto.SendMailDTO;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.AdminService;
import vn.iotstar.jobhub_hcmute_be.service.JobService;
import vn.iotstar.jobhub_hcmute_be.service.TransactionsService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.Objects;

@RestController
@PostAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin API")
public class AdminController {

    final
    UserService userService;

    final
    AdminService adminService;

    @Autowired
    JobService jobService;


    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    TransactionsService transactionsService;

    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }


    @GetMapping("/profile")
    public ResponseModel getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String jwt = authorizationHeader.substring(7);
            String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
            actionResult = userService.getProfileAdmin(userId);
        } catch (ClassCastException e) {
            actionResult.setErrorCode(ErrorCodeEnum.UNAUTHORIZED);
        }
        return responseBuild.build(actionResult);
    }

    @PostMapping("/event/add")
    public ResponseModel addEvent(@Valid @RequestBody EventDto eventDto, @RequestHeader("Authorization") String authorizationHeader) {

        ActionResult actionResult = new ActionResult();
        try {
            String jwt = authorizationHeader.substring(7);
            String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
            actionResult = adminService.addNewEvent(eventDto, userId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }


    //@PostAuthorize("hasRole('ADMIN')")
    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/user/get-list")
    public ResponseEntity<?> getListUser(@RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                         @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        return userService.getAccounts(size, page);
    }

    @GetMapping("/get-user")
    public ResponseModel getAccounts(
            @RequestParam(name = "role", required = false, defaultValue = "ALL") String role,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "index", required = false, defaultValue = "0") int index
    ) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.getAccounts(size, index, role, active);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
        }
        return responseBuild.build(actionResult);
    }


    @PostMapping("/employer/verify")
    public ResponseModel acceptEmployer(@RequestBody EmployerRequest employerRequest, BindingResult bindingResult) throws Exception {
        ActionResult actionResult;
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult
                            .getFieldError())
                    .getDefaultMessage()
            );
        }
        actionResult = adminService.changeStateEmployer(employerRequest);
        return responseBuild.build(actionResult);
    }


    @PutMapping("/job/{jobId}/change-state")
    public ResponseModel changeStateJob(@PathVariable String jobId) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.changeStateJob(jobId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("dashboard")
    public ResponseModel getDashboard(@RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String jwt = authorizationHeader.substring(7);
            String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
            actionResult = adminService.getDashBoard(userId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("transaction/get-all")
    public ResponseModel getAllTransaction(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = transactionsService.getAllTransaction(null, PageRequest.of(index, size));
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("transaction/code/count")
    public ResponseModel getCountTransaction() {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = transactionsService.getCountTransactions();
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PostMapping("user/active")
    public ResponseModel activeUser(@RequestBody ActiveUserRequest activeUserRequest) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.changeStateActive(activeUserRequest.getUserId());
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PostMapping("user/reply/active")
    public ResponseModel replyActiveUser(@RequestBody SendMailDTO sendMailDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = userService.replyActive(sendMailDTO);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

}
