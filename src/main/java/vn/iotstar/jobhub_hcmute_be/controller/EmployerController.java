package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.EmployerService;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;
import vn.iotstar.jobhub_hcmute_be.service.JobService;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


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

    @Autowired
    JobApplyService jobApplyService;

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
    public ResponseModel addJob(@RequestHeader("Authorization") String authorizationHeader,
                                @Valid @RequestBody PostJobRequest jobRequest,
                                BindingResult bindingResult) throws Exception {
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
            System.out.println(e.getMessage());
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
    public ResponseEntity<GenericResponse> uploadAvatar(@RequestParam MultipartFile imageFile,
                                                        @RequestHeader("Authorization") String token) throws IOException {
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return employerService.changeLogo(userId, imageFile);
    }


    @PutMapping("/add-bg")
    public ResponseModel uploadBackground(@RequestParam MultipartFile imageFile,
                                                        @RequestHeader("Authorization") String token) throws IOException {
        ActionResult actionResult = new ActionResult();
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        try {
            actionResult = employerService.addBg(userId, imageFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/delete-bg")
    public ResponseModel deleteItemBg(@RequestBody DeleteItemRequest deleteItemRequest,
                                          @RequestHeader("Authorization") String token) throws IOException {
        ActionResult actionResult = new ActionResult();
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        try {
            actionResult = employerService.deleteBg(userId, deleteItemRequest.getImageUrl());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }


    @PutMapping("/update-profile-company")
    public ResponseEntity<GenericResponse> update(@RequestBody EmployerUpdateDTO request,
                                                  @RequestHeader("Authorization") String authorizationHeader,
                                                  BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
        return employerService.updateCompanyProfile(userIdFromToken, request);
    }

    @GetMapping("/get-applicants")
    public ResponseModel getAllByUserIdAndDateFilters(@RequestParam(defaultValue = "0") int index,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "-1") int day,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
            if(day < 0){
                actionResult = employerService.getApplicants(userIdFromToken, PageRequest.of(index, size), state);
            }
            else
                actionResult = jobApplyService.getAllByUserIdAndDateFilters( PageRequest.of(index, size), userIdFromToken, day, state);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/get-applicants/{jobId}")
    public ResponseModel getAllJobByJobId(@PathVariable("jobId") String jobId,
                                          @RequestParam(defaultValue = "0") int index,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestHeader("Authorization") String authorizationHeader ) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = jobApplyService.getAllByJobIdAndEmployerId(PageRequest.of(index, size), jobId, userIdFromToken, state);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("student/state/{usedId}")
    public ResponseModel updateCandidateState(@RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable String usedId,
                                              @Valid @RequestBody UpdateStateRequest updateStateRequest){
        ActionResult actionResult = new ActionResult();
        try
        {
            String token = authorizationHeader.substring(7);
            String recruiterId = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = employerService.updateCandidateState(recruiterId, usedId, updateStateRequest);
            if(updateStateRequest.getReplyRequest() != null)
                employerService.reply(updateStateRequest.getReplyRequest());
        }
        catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PostMapping("/reply")
    public ResponseModel reply( @RequestBody ReplyRequest replyRequest) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = employerService.replyCandidate(replyRequest);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @DeleteMapping("/job/{jobId}")
    public ResponseEntity<GenericResponse> deleteJob(@PathVariable("jobId") String jobId) {
        try {
            Optional<Job> job = jobService.findById(jobId);
            if (job.isPresent()) {
                jobService.delete(job.get());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(GenericResponse.builder()
                                .success(true)
                                .message("Deleted Job")
                                .result(null)
                                .statusCode(HttpStatus.OK.value())
                                .build());
            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Job Not Found")
                                .result(null)
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Request url error")
                            .result(null)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        } catch (Exception e) {
            String mes = "Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(mes)
                            .result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

}
