package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerUpdateDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.PostJobRequest;
import vn.iotstar.jobhub_hcmute_be.dto.UserUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.EmployerService;
import vn.iotstar.jobhub_hcmute_be.service.JobService;

import java.io.IOException;
import java.util.Objects;


@RestController
@PreAuthorize("hasRole('EMPLOYER')")
@RequestMapping("/api/v1/employer")
@Tag(name="Employer", description="Employer API")
public class EmployerController {

    @Autowired
    JobService jobService;
    //@PreAuthorize("hasRole('EMPLOYER')")

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    EmployerService employerService;

    //post 1 job
    @PostMapping("/post-job")
    public ResponseEntity<?> addJob(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody PostJobRequest jobRequest, BindingResult bindingResult) throws Exception {

        String jwt = authorizationHeader.substring(7);
        String employerId = jwtTokenProvider.getUserIdFromJwt(jwt);
        // Kiểm tra xem các trường bắt buộc có được điền hay không
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult
                            .getFieldError())
                    .getDefaultMessage()
            );
        }
        return jobService.postJob(jobRequest, employerId);
    }

    @PutMapping("/change-logo")
    public ResponseEntity<GenericResponse> uploadAvatar(@RequestParam MultipartFile imageFile,
                                                        @RequestHeader("Authorization") String token)
            throws IOException {

        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return employerService.changeLogo(userId, imageFile);
    }
    @PutMapping("/update-profile-company")
    public ResponseEntity<GenericResponse> update(
            @RequestBody EmployerUpdateDTO request,
            @RequestHeader("Authorization") String authorizationHeader,
            BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult
                            .getFieldError())
                    .getDefaultMessage()
            );
        }

        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
        return employerService.updateCompanyProfile(userIdFromToken, request);
    }

}
