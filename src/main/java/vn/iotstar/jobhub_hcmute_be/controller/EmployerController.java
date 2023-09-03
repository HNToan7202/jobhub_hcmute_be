package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.PostJobRequest;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.JobService;


@RestController
@PreAuthorize("hasRole('EMPLOYER')")
@RequestMapping("/api/v1/employer")
@Tag(name="Employer", description="Employer API")
public class EmployerController {

    @Autowired
    JobService jobService;
    //@PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("/post-job")
    public ResponseEntity<?> addJob(@Valid @RequestBody PostJobRequest jobRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("Unauthorized: Empty or invalid token")
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .build()
                    );
        }
        return jobService.postJob(jobRequest, userDetail.getUser().getUserId());
    }
}
