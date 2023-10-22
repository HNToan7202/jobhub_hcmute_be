package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerUpdateDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.JobUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.dto.PostJobRequest;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.EmployerService;
import vn.iotstar.jobhub_hcmute_be.service.JobService;
import java.io.IOException;
import java.util.Objects;


@RestController
@PreAuthorize("hasRole('EMPLOYER')")
@RequestMapping("/api/v1/employer")
@Tag(name = "Employer", description = "Employer API")
public class EmployerController {

    final JobService jobService;
    private final JwtTokenProvider jwtTokenProvider;

    final EmployerService employerService;


    @Autowired
    ResponseBuild responseBuild;

    public EmployerController(JobService jobService, JwtTokenProvider jwtTokenProvider, EmployerService employerService) {
        this.jobService = jobService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.employerService = employerService;
    }

    @GetMapping("/profile")
    //@Operation(security = {@SecurityRequirement(name = "api-key")}) // Yêu cầu xác thực bằng API key
    public ResponseEntity<GenericResponse> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return employerService.getProfile(userId);
    }


    @Transactional
    //post 1 job
    @PostMapping("/post-job")
    public ResponseModel addJob(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody PostJobRequest jobRequest, BindingResult bindingResult) throws Exception {
        ActionResult actionResult = new ActionResult();
        String jwt = authorizationHeader.substring(7);
        String employerId = jwtTokenProvider.getUserIdFromJwt(jwt);
        // Kiểm tra xem các trường bắt buộc có được điền hay không
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        try {
            actionResult = jobService.postJob(jobRequest, employerId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/update-job/{jobId}")
    public ResponseModel updateJob(@PathVariable("jobId") String jobId,
                                   @Valid @RequestBody JobUpdateRequest jobUpdateRequest,
                                   @RequestHeader("Authorization") String authorizationHeader,
                                   BindingResult bindingResult) {
        String token = authorizationHeader.substring(7);
        String recruiterId = jwtTokenProvider.getUserIdFromJwt(token);
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.updateJob(jobId, jobUpdateRequest, recruiterId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }


    @PutMapping("/change-logo")
    public ResponseEntity<GenericResponse> uploadAvatar(@RequestParam MultipartFile imageFile, @RequestHeader("Authorization") String token) throws IOException {

        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return employerService.changeLogo(userId, imageFile);
    }

    @PutMapping("/update-profile-company")
    public ResponseEntity<GenericResponse> update(@RequestBody EmployerUpdateDTO request, @RequestHeader("Authorization") String authorizationHeader, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
        return employerService.updateCompanyProfile(userIdFromToken, request);
    }

}
